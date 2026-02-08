package com.cloud.auth_service.application.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cloud.auth_service.application.dto.UserAccessDTO;
import com.cloud.auth_service.domain.model.Permission;
import com.cloud.auth_service.domain.model.UserVendorAccess;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserVendorAccessRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author nhutphuong
 * @version 1
 * @created 8/02/2025 20:30h
 */
@Service
@RequiredArgsConstructor
public class UserVendorAccessService {

    private final UserVendorAccessRepository accessRepository;

    public UserAccessDTO getUserAccessInfo(UUID userId) {
        UserVendorAccess access = accessRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User này chưa được gán vào bất kỳ Vendor nào!"));

        Set<String> permissions = access.getRole().getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        return UserAccessDTO.builder()
                .userId(userId)
                .vendorId(access.getId().getVendorId())
                .roleCode(access.getRole().getCode())
                .permissions(permissions)
                .build();
    }

    public boolean hasPermission(UUID userId, String permissionCode) {
        return accessRepository.findByUserId(userId)
                .map(access -> access.getRole().getPermissions().stream()
                .anyMatch(p -> p.getCode().equals(permissionCode)))
                .orElse(false);
    }
}