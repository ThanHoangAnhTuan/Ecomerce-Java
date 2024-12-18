package com.thantuan.backend.security;

import com.thantuan.backend.service.CookieService;
import com.thantuan.backend.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        List<String> pathNotUseJwtFilter = Arrays.asList("/api/login", "/api/auth/register", "/api/auth/login",
                "/api/auth/logout", "/api/auth/activate-account", "/api/auth/activate-account-regain",
                "/api/auth/request-reset-password", "/api/auth/verify-reset-password-token", "/api/auth/reset-password",
                "/api/auth/login/google", "/favicon.ico", "/api/products/get-all-products",
                "/api/products/get-product-by-id", "/api/products/get-product-by-category-id",
                "/api/products/search-product", "/api/category/get-all-categories", "/api/category/get-category-by-id",
                "/api/users/init-roles", "/api/auth/login/google/success", "/api/auth/login/google/failure",
                "/api/review/get-all-review-by-product-id",
                "/swagger-ui",
                "/v2/api-docs",
                "/v3/api-docs",
                "/swagger-resources",
                "/configuration/ui",
                "/configuration/security",
                "/webjars",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/favicon.ico");
        if (pathNotUseJwtFilter.stream().anyMatch(request.getServletPath()::contains)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessTokenFromCookie = cookieService.getJwtFromCookie(request, "jwt");
        String refreshTokenFromCookie = cookieService.getJwtFromCookie(request, "jwt_refresh");

        String username = null;
        String refreshUsername;
        Set<String> roles;

        if (accessTokenFromCookie == null || refreshTokenFromCookie == null) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Missing JWT token. Please login.\",\n\"status\": "
                    + HttpServletResponse.SC_UNAUTHORIZED + "}");
            return;
        }

        try {
            username = jwtService.extractUserName(accessTokenFromCookie);
        } catch (ExpiredJwtException expiredJwtException) {
            try {
                refreshUsername = jwtService.extractUserName(refreshTokenFromCookie);
                roles = jwtService.extractRoles(refreshTokenFromCookie);
                String name = jwtService.extractName(refreshTokenFromCookie);
                String image = jwtService.extractImage(refreshTokenFromCookie);
                Long id = jwtService.extractId(refreshTokenFromCookie);
                accessTokenFromCookie = jwtService.generateToken(refreshUsername, roles, name, image, id);
                refreshTokenFromCookie = jwtService.generateRefreshToken(refreshUsername, roles, name, image, id);
                cookieService.setJwtInCookie(response, accessTokenFromCookie);
                cookieService.setRefreshJwtInCookie(response, refreshTokenFromCookie);
                username = jwtService.extractUserName(accessTokenFromCookie);
            } catch (ExpiredJwtException expiredJwtException1) {
                cookieService.resetCookie(response);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Token Expired. Please login again!\",\n\"status\": "
                        + HttpServletResponse.SC_UNAUTHORIZED + "}");
                return;
            }
        } catch (JwtException e) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": " + e.getMessage() + "\",\n\"status\": "
                    + HttpServletResponse.SC_UNAUTHORIZED + "}");
        }

        // Nếu username không phải null và chưa có xác thực trong SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!userDetails.isEnabled()) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Account is not activated. Please activate your account.\","
                        + "\n\"status\": " + HttpServletResponse.SC_UNAUTHORIZED + "}");
                return;
            }
            if (jwtService.validateToken(accessTokenFromCookie, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Invalid token. Please login again.\",\n\"status\": "
                        + HttpServletResponse.SC_UNAUTHORIZED + "}");
            }
        }
        filterChain.doFilter(request, response);
    }
}
