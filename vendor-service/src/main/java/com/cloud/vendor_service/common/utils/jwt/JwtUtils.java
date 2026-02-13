package com.cloud.vendor_service.common.utils.jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

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

    public Optional<String> getTokenFromContextHolder() {
        return Optional.ofNullable(getJwt()).map(Jwt::getTokenValue);
    }

    public Optional<String> getCurrentUsername() {
        Authentication auth = getAuthentication();
        return auth != null ? Optional.of(auth.getName()) : Optional.empty();
    }

    public Optional<String> getCurrentFullName() {
        Jwt jwt = getJwt();
        return jwt != null ? Optional.ofNullable(jwt.getClaim("name")) : Optional.empty();
    }

    public Optional<Instant> getCurrentExpireAt() {
        Jwt jwt = getJwt();
        return jwt != null ? Optional.of(jwt.getExpiresAt()) : Optional.empty();
    }

    public Optional<Instant> getCurrentIssueAt() {
        Jwt jwt = getJwt();
        return jwt != null ? Optional.of(jwt.getIssuedAt()) : Optional.empty();
    }

    public Optional<UUID> getCurrentUserId() {
        return Optional.ofNullable(getJwt())
            .map(jwt -> jwt.getClaim("sub"));
    }

    public Optional<String> getCurrentUserEmail() {
        Jwt jwt = getJwt();
        return jwt != null ? Optional.ofNullable(jwt.getClaim("email")) : Optional.empty();
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
