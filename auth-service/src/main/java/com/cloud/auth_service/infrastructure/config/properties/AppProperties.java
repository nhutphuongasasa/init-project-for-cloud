package com.cloud.auth_service.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @nhutphuong
 * @since 2026/1/11 12h
 * @version 1
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nhutphuongasasa")
public class AppProperties {

    private final Frontend frontend = new Frontend();
    private final Security security = new Security();
	private String aesKey;

    @Data
    public static class Frontend {
        private String callbackUrl;
    }

    @Data
    public static class Security {
        private long accessTokenValidityInSeconds;
        private String jwtIssuer;
    }
}