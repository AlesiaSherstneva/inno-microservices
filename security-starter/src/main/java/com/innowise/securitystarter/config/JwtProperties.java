package com.innowise.securitystarter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Long accessTokenExpiration = 3_600_000L; // 1 hour
    private Long refreshTokenExpiration = 604_800_000L; // 1 week
}