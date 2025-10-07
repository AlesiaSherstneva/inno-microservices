package com.innowise.securitystarter.jwt;

import com.innowise.securitystarter.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    private static final String USER_ID_CLAIM = "userId";
    private static final String ROLE_CLAIM = "role";

    private SecretKey getSigningKey() {
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        }
        return secretKey;
    }

    public String generateAccessToken(String phoneNumber, Long userId, String role) {
        return Jwts.builder()
                .subject(phoneNumber)
                .claim(USER_ID_CLAIM, userId)
                .claim(ROLE_CLAIM, role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String phoneNumber) {
        return Jwts.builder()
                .subject(phoneNumber)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}