package com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.fallback;

import com.cloud.vendor_service.application.dto.response.UserResponse;
import com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.client.AuthClient;
import com.cloud.vendor_service.infrastructure.config.properties.AuthClientProperties;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author nhutphuong
 * @since 2025-02-8 11:04
 * @version 1.0`
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {
    private final AuthClientProperties authClientProperties;

    @Override
    public AuthClient create(Throwable cause) {
        return new AuthClientFallback(cause);
    }

    private class AuthClientFallback implements AuthClient {

        private final Throwable cause;

        public AuthClientFallback(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public UserResponse getUserByEmail(String email) {
            log.warn("Fallback triggered for getUserByEmail(email={})", email, cause);

            logDetailedCause(cause);

            String fallbackName = determineFallbackName(cause);

            return UserResponse.builder()
                .provider(authClientProperties.getDEFAULT_PROVIDER())
                .email(email != null ? email : authClientProperties.getDEFAULT_EMAIL())
                .fullName(fallbackName)
                .avatarUrl(authClientProperties.getDEFAULT_AVATAR())
                .build();
        }

        private void logDetailedCause(Throwable cause) {
            if (cause instanceof FeignException fe) {
                log.warn("Feign error → status: {}, message: {}", fe.status(), fe.getMessage());
                log.debug("Response body: {}", fe.contentUTF8());
            } else if (cause instanceof CallNotPermittedException) {
                log.warn("CircuitBreaker is OPEN → skipping call to auth-service");
            } else {
                log.warn("Fallback cause", cause);
            }
        }

        private String determineFallbackName(Throwable cause) {
            if (cause instanceof CallNotPermittedException) {
                return "Service Unavailable (Circuit Open)";
            }
            if (cause instanceof FeignException fe && fe.status() >= 500) {
                return "Service Error (Server Side)";
            }
            return authClientProperties.getDEFAULT_FULL_NAME();
        }
    }
}