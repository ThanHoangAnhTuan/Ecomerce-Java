package com.thantuan.backend.controller;

import com.thantuan.backend.dto.*;
import com.thantuan.backend.exception.EmailAlreadyExistsException;
import com.thantuan.backend.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Valid RegisterRequest registerRequest,
                                             HttpServletResponse response) throws MessagingException {
        return ResponseEntity.ok(authService.register(registerRequest, response));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<Response> activateAccount(@RequestParam("email") String email,
                                                    @RequestParam("token") String token,
                                                    HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.activateAccount(email, token, httpServletResponse));
    }

    @GetMapping("/activate-account-regain/")
    public ResponseEntity<Response> activateAccountRegain(@RequestParam("email") String email) throws MessagingException {
        return ResponseEntity.ok(authService.activateAccountRegain(email));
    }

    @GetMapping("/request-reset-password")
    public ResponseEntity<Response> requestResetPassword(@RequestParam("email") String email) throws MessagingException {
        return ResponseEntity.ok(authService.requestResetPassword(email));
    }

    @GetMapping("/verify-reset-password-token")
    public ResponseEntity<Response> verifyResetPasswordToken(@RequestParam("email") String email,
                                                             @RequestParam("token") String token) {
        return ResponseEntity.ok(authService.verifyResetPasswordToken(email, token));
    }

    @GetMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestParam("email") String email,
                                                  @RequestParam("token") String token,
                                                  @RequestBody @Valid ResetPasswordDtoRequest request) {
        String newPassword = request.getPassword();
        return ResponseEntity.ok(authService.resetPassword(email, token, newPassword));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody @Valid LoginRequest loginRequest,
                                          HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.login(loginRequest, httpServletResponse));
    }

    @GetMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(response));
    }

    @GetMapping("/login/google/success")
    public void loginGoogleSuccess(OAuth2AuthenticationToken oAuth2AuthenticationToken, HttpServletResponse response)
            throws IOException {
        try {
            authService.loginWithOAuth2Success(oAuth2AuthenticationToken, response);
            response.sendRedirect("http://localhost:3000");
        } catch (EmailAlreadyExistsException e) {
            response.sendRedirect("http://localhost:3000/login?error=Email is already registered with another provider");
        }
    }

    @GetMapping("/login/google/failure")
    public ResponseEntity<Response> loginGoogleFailure(AuthenticationException authException) {
        return ResponseEntity.ok(authService.loginWithOAuth2Failure(authException));
    }
}
