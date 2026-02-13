package com.cloud.vendor_service.infrastructure.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;

@Configuration
@EnableFeignClients(basePackages = "com.cloud.vendor_service.infastructure.outbound.client")
public class FeignClientConfig {

    /**
     * add access token to header of feign client request
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                String token = jwtAuthenticationToken.getToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }

    /**
     * config timeout for feign client
     */
    @Bean
    public feign.Request.Options feignRequestOptions() {
        return new feign.Request.Options(
            10000, TimeUnit.MILLISECONDS,
            60000, TimeUnit.MILLISECONDS,
            true
        );
    }

    /**
     * config retry for feign client
     */
    @Bean
    public Retryer feignRetryer() {
        return new feign.Retryer.Default(
            1000,//khoang thoi gian cho retry lan dau 
            10000,//khaong thoi gian cho retry toi da co the tang
            3
        );
    }

    /**
     * config log for feign client
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
