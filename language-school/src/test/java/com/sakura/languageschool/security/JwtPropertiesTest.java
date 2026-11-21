package com.sakura.languageschool.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

class JwtPropertiesTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(TestConfiguration.class)
                    .withPropertyValues(
                            "app.security.jwt.secret=test-only-secret",
                            "app.security.jwt.access-token-ttl=45m");

    @Test
    void bindsJwtConfiguration() {
        contextRunner.run(context -> {
            JwtProperties properties = context.getBean(JwtProperties.class);

            assertThat(properties.getSecret()).isEqualTo("test-only-secret");
            assertThat(properties.getAccessTokenTtl())
                    .isEqualTo(Duration.ofMinutes(45));
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(JwtProperties.class)
    static class TestConfiguration {
    }
}
