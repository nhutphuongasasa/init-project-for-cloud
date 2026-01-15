package com.cloud.vendor_service.common.utils.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.cloud.vendor_service.common.utils.jwt.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {
    private final JwtUtils jwtUtils;

    public UUID getVendorIdFromAuthentication(){
        UUID vendorId = UUID.fromString(jwtUtils.getCurrentUserId());
        
        if(vendorId == null){
            log.warn("Cannot proceed: vendorId is null - no authenticated user found in security context");
            throw new IllegalStateException("No authenticated user found in security context");
        }

        return vendorId;
    }

    public String getEmailFromAuthentication(){
        String email = jwtUtils.getCurrentUserEmail();
        
        if(email == null){
            log.warn("Cannot proceed: email is null - no authenticated user found in security context");
            throw new IllegalStateException("No authenticated user found in security context");
        }

        return email;
    }
}
