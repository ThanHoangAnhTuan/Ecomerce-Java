package com.thantuan.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/api/auth/register",
                                    "/api/auth/logout",
                                    "/api/auth/login/**",
                                    "/api/auth/login/google/**",
                                    "/oauth2/authorization/google/",
                                    "/api/auth/activate-account/**",
                                    "/favicon.ico",
                                    "/api/auth/activate-account-regain/**",
                                    "/api/products/get-all-products",
                                    "/api/products/get-product-by-id/**",
                                    "/api/products/get-product-by-category-id/**",
                                    "/api/products/search-product/**",
                                    "/api/category/get-all-categories",
                                    "/api/category/get-category-by-id/**",
                                    "/api/users/init-roles",
                                    "/api/auth/request-reset-password",
                                    "/api/auth/verify-reset-password-token",
                                    "/api/auth/reset-password",
                                    "/api/order/**",
                                    "/api/review/get-all-review-by-product-id/**",
                                    "/swagger-ui/**",
                                    "/v2/api-docs/**",
                                    "/v3/api-docs/**",
                                    "/swagger-resources/**",
                                    "/configuration/ui/**",
                                    "/configuration/security/**",
                                    "/webjars/**",
                                    "/swagger-ui.html/**")
                            .permitAll();
                    req
                            .anyRequest()
                            .authenticated();
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(customizer -> customizer
                        .defaultSuccessUrl("/api/auth/login/google/success", true)
                        .failureUrl("/api/auth/login/google/failure"))
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
