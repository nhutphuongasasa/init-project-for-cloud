package com.cloud.vendor_service.common.utils.jwt;

import com.cloud.vendor_service.common.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/***
 * Helper class to get security context information
 * @author nhutphuong
 * @version 1.0
 * @created 11/2/2026
 */
@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final JwtUtils jwtUtils;

    public UUID currentUserId() {
        return jwtUtils.getCurrentUserId()
            .orElseThrow(() -> new UnAuthorizedException("User identity (sub) is required."));
    }

    public UUID currentVendorId() {
        return jwtUtils.getCurrentVendorId()
            .orElseThrow(() -> new UnAuthorizedException("Vendor context (vendor_id) is required."));
    }

    public String currentUserEmail() {
        return jwtUtils.getCurrentUserEmail()
            .orElseThrow(() -> new UnAuthorizedException("User email is required."));
    }

    public String currentUsername() {
        return jwtUtils.getCurrentUsername().orElse("anonymous");
    }

    public String fullName() {
        return jwtUtils.getCurrentFullName().orElse("Unknown User");
    }

    public Instant tokenExpiresAt() {
        return jwtUtils.getCurrentExpireAt().orElse(null);
    }

    public boolean hasSystemRole(String role) {
        String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return jwtUtils.getSystemRoles().contains(formattedRole);
    }

    public boolean hasVendorPermission(String permission) {
        return jwtUtils.getVendorPermissions().contains(permission);
    }

    public boolean isMemberOfVendor(UUID vendorId) {
        return jwtUtils.getVendorIds().contains(vendorId.toString());
    }

    public List<String> getAllVendorIds() {
        return jwtUtils.getVendorIds();
    }
}