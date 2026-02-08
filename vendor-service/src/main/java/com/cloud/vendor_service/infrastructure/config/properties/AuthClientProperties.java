package com.cloud.vendor_service.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @nhutphuong
 * @since 2026/1/11 11:15
 * @version 1
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nhutphuongasasa")
public class AuthClientProperties {
    private  String DEFAULT_PROVIDER;
    private  String DEFAULT_EMAIL;
    private  String DEFAULT_FULL_NAME;
    private  String DEFAULT_AVATAR;
}