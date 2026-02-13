package com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import com.cloud.vendor_service.application.dto.response.UserResponse;
import com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.fallback.AuthClientFallbackFactory;
import com.cloud.vendor_service.infrastructure.config.FeignClientConfig;

import jakarta.validation.Valid;

@FeignClient(
    name = "auth-service",
    configuration = FeignClientConfig.class,
    fallbackFactory = AuthClientFallbackFactory.class  
)
@Validated
public interface AuthClient{
    
    @Valid
    @GetMapping()
    UserResponse getUserByEmail(String email);
}
