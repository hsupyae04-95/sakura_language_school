package com.sakura.languageschool.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SECRET =
            "01234567890123456789012345678901";

    @Test
    void generatesAndValidatesTokenWithRequiredTimeClaims() {
        JwtService service = new JwtService(properties(Duration.ofHours(1)));

        String token = service.generateAccessToken("student1");
        Claims claims = service.validateAndGetClaims(token);

        assertThat(claims.getSubject()).isEqualTo("student1");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
        assertThat(claims).doesNotContainKeys("email", "password");
    }

    @Test
    void rejectsExpiredToken() {
        JwtService service = new JwtService(properties(Duration.ofHours(1)));
        Instant expiredAt = Instant.now().minusSeconds(60);

        String token = Jwts.builder()
                .subject("student1")
                .issuedAt(Date.from(expiredAt.minusSeconds(60)))
                .expiration(Date.from(expiredAt))
                .signWith(signingKey())
                .compact();

        assertThatThrownBy(() -> service.validateAndGetClaims(token))
                .isInstanceOf(JwtTokenValidationException.class)
                .hasMessage("JWT has expired");
    }

    @Test
    void rejectsMalformedToken() {
        JwtService service = new JwtService(properties(Duration.ofHours(1)));

        assertThatThrownBy(() -> service.validateAndGetClaims("not-a-jwt"))
                .isInstanceOf(JwtTokenValidationException.class)
                .hasMessage("JWT is invalid");
    }

    @Test
    void rejectsUnsupportedAlgorithm() {
        JwtService service = new JwtService(properties(Duration.ofHours(1)));
        String header = encode("{\"alg\":\"none\",\"typ\":\"JWT\"}");
        String payload = encode("{\"sub\":\"student1\"}");

        assertThatThrownBy(() -> service.validateAndGetClaims(
                header + "." + payload + "."))
                .isInstanceOf(JwtTokenValidationException.class);
    }

    @Test
    void rejectsTokenWithIncorrectSignature() {
        JwtService service = new JwtService(properties(Duration.ofHours(1)));
        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "abcdefghijklmnopqrstuvwxyz123456".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("student1")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(wrongKey)
                .compact();

        assertThatThrownBy(() -> service.validateAndGetClaims(token))
                .isInstanceOf(JwtTokenValidationException.class)
                .hasMessage("JWT is invalid");
    }

    @Test
    void refusesToIssueTokenWithoutUsableSecret() {
        JwtProperties properties = properties(Duration.ofHours(1));
        properties.setSecret("");
        assertThatThrownBy(() -> new JwtService(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT signing secret must be supplied through configuration");
    }

    private static JwtProperties properties(Duration accessTokenTtl) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setAccessTokenTtl(accessTokenTtl);
        return properties;
    }

    private static SecretKey signingKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
