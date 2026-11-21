package com.sakura.languageschool.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;

@Service
public class JwtService {

    private static final int MINIMUM_SECRET_BYTES = 32;

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        signingKey();
    }

    public String generateAccessToken(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("JWT subject must not be blank");
        }

        Instant issuedAt = Instant.now();
        Duration tokenLifetime = properties.getAccessTokenTtl();
        if (tokenLifetime == null || tokenLifetime.isZero() || tokenLifetime.isNegative()) {
            throw new IllegalStateException("JWT access-token lifetime must be positive");
        }

        Instant expiresAt = issuedAt.plus(tokenLifetime);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey())
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtTokenValidationException("JWT must not be blank", null);
        }

        try {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException exception) {
            throw new JwtTokenValidationException("JWT has expired", exception);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtTokenValidationException("JWT is invalid", exception);
        }
    }

    private SecretKey signingKey() {
        String secret = properties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "JWT signing secret must be supplied through configuration");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT signing secret must be at least 32 UTF-8 bytes", 
                    new WeakKeyException("JWT signing secret is too short"));
        }

        return Keys.hmacShaKeyFor(secretBytes);
    }
}
