package com.thantuan.backend.service;

import com.thantuan.backend.dto.*;
import com.thantuan.backend.entity.Role;
import com.thantuan.backend.entity.Token;
import com.thantuan.backend.entity.User;
import com.thantuan.backend.enums.AuthProvider;
import com.thantuan.backend.exception.AccountIsEnabledException;
import com.thantuan.backend.exception.EmailAlreadyExistsException;
import com.thantuan.backend.exception.InvalidTokenException;
import com.thantuan.backend.mapper.EntityDtoMapper;
import com.thantuan.backend.repository.IRoleRepo;
import com.thantuan.backend.repository.ITokenRepo;
import com.thantuan.backend.repository.IUserRepo;
import com.thantuan.backend.security.UserDetailsServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EntityDtoMapper entityDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final EmailService emailService;
    private final ITokenRepo tokenRepo;
    private final IRoleRepo roleRepo;
    private final UserDetailsServiceImpl userDetailsService;

    @NonNull
    private UserDto addJwtToCookieAndReturnUserDto(User user, HttpServletResponse response) {
        UserDto userDto = entityDtoMapper.mapUserToUserDto(user);
        String token = jwtService.generateToken(userDto.getEmail(),
                                                userDto.getRoles(),
                                                user.getName(),
                                                user.getImage(),
                                                user.getId());
        String refreshToken = jwtService.generateRefreshToken(userDto.getEmail(),
                                                                userDto.getRoles(),
                                                                user.getName(),
                                                                user.getImage(),
                                                                user.getId());
        cookieService.setJwtInCookie(response, token);
        cookieService.setRefreshJwtInCookie(response, refreshToken);
        return userDto;
    }

    public Response register(@NonNull RegisterRequest registerRequest, HttpServletResponse response)
            throws MessagingException {
        User userAlreadyExists = userRepo.findByEmail(registerRequest.getEmail());
        if (userAlreadyExists != null) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        Role userRole = roleRepo.findByName("BUYER")
                                .orElseThrow(() -> new IllegalArgumentException("Role BUYER was not initialized"));
        User user = User.builder()
                        .name(registerRequest.getUsername())
                        .phone(registerRequest.getPhone())
                        .address(registerRequest.getAddress())
                        .roles(Set.of(userRole))
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .email(registerRequest.getEmail())
                        .provider(AuthProvider.LOCAL)
                        .accountLocked(false)
                        .enabled(false)
                        .build();
        User savedUser = userRepo.save(user);
        emailService.sendValidationEmail(user, "Mã xác thực để hoàn tất đăng ký account");
        UserDto userDto = addJwtToCookieAndReturnUserDto(savedUser, response);
        return Response.builder()
                    .status(CREATED.value())
                    .message("Please verify your email")
                    .user(userDto)
                    .build();
    }

    @Transactional
    public Response activateAccount(String email, String verificationCode, HttpServletResponse response) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (user.isEnabled()) {
            throw new AccountIsEnabledException("Account is enabled");
        }
        Token savedToken = this.checkTokenForResetPassword(user.getId(), verificationCode);

        user.setEnabled(true);
        User savedUser = userRepo.save(user);
        tokenRepo.delete(savedToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDto userDto = this.addJwtToCookieAndReturnUserDto(savedUser, response);

        return Response.builder()
                    .status(CREATED.value())
                    .message("User activated successfully")
                    .user(userDto)
                    .build();
    }

    public Response activateAccountRegain(String email) throws MessagingException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (user.isEnabled()) {
            throw new AccountIsEnabledException("Account is enabled");
        }
        emailService.sendValidationEmail(user, "Mã xác thực để hoàn tất đăng ký account");
        return Response.builder()
                    .status(OK.value())
                    .message("Please verify your email")
                    .build();
    }

    public Response login(@Valid @NonNull LoginRequest loginRequest, HttpServletResponse response) {
        User user = userRepo.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!user.isEnabled()) {
            UserDto userDto = this.addJwtToCookieAndReturnUserDto(user, response);
            return Response.builder()
                        .user(userDto)
                        .status(UNAUTHORIZED.value())
                        .message("Account not activated. Please activate your account.")
                        .build();
        }
        if (user.getProvider() == AuthProvider.GOOGLE) {
            throw new EmailAlreadyExistsException("This account is already signed in with google");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDto userDto = this.addJwtToCookieAndReturnUserDto(user, response);

        return Response.builder()
                    .status(OK.value())
                    .message("User logged in successfully")
                    .user(userDto)
                    .build();
    }

    public Response logout(HttpServletResponse response) {
        cookieService.resetCookie(response);
        return Response.builder()
                    .status(OK.value())
                    .message("User logged out successfully")
                    .build();
    }

    public Response requestResetPassword(String email) throws MessagingException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        emailService.sendValidationEmail(user, "Mã xác thực để reset password");
        UserDto userDto = entityDtoMapper.mapUserToUserDto(user);
        return Response.builder()
                    .status(CREATED.value())
                    .message("Please verify your email to reset password")
                    .user(userDto)
                    .build();
    }

    public Response verifyResetPasswordToken(String email, String token) {
        User user = this.checkUserForResetPassword(email);
        this.checkTokenForResetPassword(user.getId(), token);
        return Response.builder()
                    .status(CREATED.value())
                    .message("Please create a new password")
                    .build();
    }

    @NonNull
    private User checkUserForResetPassword(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @NonNull
    private Token checkTokenForResetPassword(@NonNull Long userId, String token) {
        Token savedToken = tokenRepo.findByToken(token)
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (!userId.equals(savedToken.getUser().getId())) {
            throw new InvalidTokenException("Invalid token");
        }
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            throw new InvalidTokenException("Reset password token expired. Please request a resend.");
        }
        return savedToken;
    }

    @Transactional
    public Response resetPassword(String email, String token, String newPassword) {
        User user = this.checkUserForResetPassword(email);
        Token savedToken = this.checkTokenForResetPassword(user.getId(), token);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        tokenRepo.delete(savedToken);
        return Response.builder()
                    .status(OK.value())
                    .message("User reset password successfully")
                    .build();
    }

    public void loginWithOAuth2Success(OAuth2AuthenticationToken oAuth2AuthenticationToken, HttpServletResponse response) throws IOException {
        User savedUser = null;
        String email = oAuth2AuthenticationToken.getPrincipal().getAttribute("email");
        String name = oAuth2AuthenticationToken.getPrincipal().getAttribute("name");
        String image = oAuth2AuthenticationToken.getPrincipal().getAttribute("picture");
        String provider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        User userAlreadyExists = userRepo.findByEmail(email);
        if (userAlreadyExists == null) {
            Role userRole = roleRepo.findByName("BUYER")
                                    .orElseThrow(() -> new IllegalArgumentException("Role BUYER was not initialized"));
            User user = User.builder()
                            .enabled(true)
                            .image(image)
                            .accountLocked(false)
                            .name(name)
                            .roles(Set.of(userRole))
                            .email(email)
                            .build();
            if (provider.equals("google")) {
                user.setProvider(AuthProvider.GOOGLE);
            }
            savedUser = userRepo.save(user);
        } else {
            if (!userAlreadyExists.getProvider().toString().toLowerCase().equals(provider)) {
                throw new EmailAlreadyExistsException("Email is already");
            }
        }
        addJwtToCookieAndReturnUserDto(Objects.requireNonNullElse(savedUser, userAlreadyExists), response);
    }

    public Response loginWithOAuth2Failure(AuthenticationException authException) {
        return Response.builder()
                    .status(UNAUTHORIZED.value())
                    .message(authException.getMessage())
                    .build();
    }
}
