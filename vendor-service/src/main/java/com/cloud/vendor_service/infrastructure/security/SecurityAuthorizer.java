package com.cloud.vendor_service.infrastructure.security;

import com.cloud.vendor_service.common.utils.jwt.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("ss")
@RequiredArgsConstructor
public class SecurityAuthorizer {

    private final SecurityHelper security;

    /***
     * check permission
     * @param permission
     * @return
     */
    public boolean hasP(String permission) {
        if (security.hasSystemRole("SYS_ADMIN") || security.hasVendorPermission("*:*")) {
            return true;
        }
        
        String module = permission.split(":")[0];
        return security.hasVendorPermission(permission) || 
            security.hasVendorPermission(module + ":*");
    }

    /***
     * check role
     * @param role
     * @return
     */
    public boolean hasR(String role) {
        return security.hasSystemRole("SYS_ADMIN") || security.hasSystemRole(role);
    }
    
    /***
     * check access to vendor
     * @param vendorId
     * @return
     * @description protect from one difference vendor accessing another vendor's data
     */
    public boolean canAccessVendor(UUID vendorId) {
        if (security.hasSystemRole("SYS_ADMIN")) return true;
        
        return security.currentVendorId().equals(vendorId);
    }
}