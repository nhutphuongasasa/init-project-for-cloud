package com.cloud.vendor_service.common.utils.jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * @author nhutphuong
 * @version 1.1
 * @created 25/11/2025
 */
@Component
public final class JwtUtils {

    public List<String> getAuthorities() {
        return Optional.ofNullable(getJwt()) 
            .map(jwt -> jwt.getClaimAsStringList("authorities"))
            .orElse(Collections.emptyList());
    }

    public List<String> getSystemRoles() {
        return Optional.ofNullable(getJwt())
                .map(jwt -> jwt.getClaimAsStringList("system_roles"))
                .orElse(Collections.emptyList());
    }

    public List<String> getSystemPermissions() {
        return Optional.ofNullable(getJwt())
                .map(jwt -> jwt.getClaimAsStringList("system_authorities"))
                .orElse(Collections.emptyList());
    }

    public List<String> getVendorRoles() {
        return Optional.ofNullable(getJwt())
                .map(jwt -> jwt.getClaimAsStringList("vendor_roles"))
                .orElse(Collections.emptyList());
    }

    public List<String> getVendorPermissions() {
        return Optional.ofNullable(getJwt())
                .map(jwt -> jwt.getClaimAsStringList("vendor_authorities"))
                .orElse(Collections.emptyList());
    }

    public List<String> getVendorIds() {
        return Optional.ofNullable(getJwt())
                .map(jwt -> jwt.getClaimAsStringList("vendor_ids"))
                .orElse(Collections.emptyList());
    }

    public Optional<UUID> getCurrentVendorId() {
        return Optional.ofNullable(getJwt())
            .map(jwt -> jwt.getClaimAsString("vendor_id"))
            .map(UUID::fromString);
    }

    public String getTokenFromContextHolder() {
        Authentication auth = getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }

    public String getCurrentUsername() {
        Authentication auth = getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public String getCurrentFullName() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getClaim("name") : null;
    }

    public List<String> getCurrentRoles() {
        Authentication auth = getAuthentication();
        if (auth != null){
            return auth.getAuthorities()
                .stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toList());
        }else{
            return List.of();
        }
    }

    public Instant getCurrentExpireAt() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getExpiresAt() : null;
    }

    public Instant getCurrentIssueAt() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getIssuedAt() : null;
    }

    public Optional<UUID> getCurrentUserId() {
        return Optional.ofNullable(getJwt())
            .map(jwt -> jwt.getClaim("sub"));
    }

    public String getCurrentUserEmail() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getClaim("email") : null;
    }

    public Map<String, Object> getCurrentUserClaims() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getClaims() : null;
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Jwt getJwt() {
        Authentication auth = getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }
}
