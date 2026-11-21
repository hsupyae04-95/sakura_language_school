package com.sakura.languageschool.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed configuration for the access-token foundation.
 *
 * Token creation and secret validation are intentionally implemented in a later
 * authentication step.
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    private String secret;

    private Duration accessTokenTtl = Duration.ofHours(1);

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }
}
