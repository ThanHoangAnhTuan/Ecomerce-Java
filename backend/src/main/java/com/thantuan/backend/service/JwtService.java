package com.thantuan.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${project.jwt_secret_key}")
    private String SECRET_KEY;

    @Value("${project.jwt_cookie_expiration_time}")
    private long EXPIRATION_TIME;

    @Value("${project.jwt_cookie_refresh_token_time}")
    private long REFRESH_EXPIRATION_TIME;

    public String generateToken(String username, Set<String> roles, String name, String image, Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roles);
        claims.put("username", name);
        claims.put("image", image);
        claims.put("id", id);
        return createToken(username, claims, EXPIRATION_TIME);
    }

    public String generateRefreshToken(String username, Set<String> roles, String name, String image, Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roles);
        claims.put("username", name);
        claims.put("image", image);
        claims.put("id", id);
        return createToken(username, claims, REFRESH_EXPIRATION_TIME);
    }

    public String createToken(String username, Map<String, Object> claims, long time) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + time))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Set<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", Set.class)); // Lấy roles từ claims
    }

    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class)); // Lấy name từ claims
    }

    public String extractImage(String token) {
        return extractClaim(token, claims -> claims.get("image", String.class)); // Lấy name từ claims
    }

    public Long extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class)); // Lấy id từ claims
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername());
    }
}
