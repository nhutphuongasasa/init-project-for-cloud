package com.cloud.auth_service.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.auth_service.application.dto.response.PermissionResponse;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.PermissionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {
    
    private final PermissionRepository permissionRepository;

    public Map<String, List<PermissionResponse>> getAllPermissionsGroupedByModule() {
        return permissionRepository.findAll().stream()
                .map(p -> PermissionResponse.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .name(p.getName())
                        .module(p.getModule())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.groupingBy(PermissionResponse::getModule));
    }

    public Set<String> getAllPermissionCodes() {
        return permissionRepository.findAllCodes();
    }

    public void validatePermissions(Set<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) return;
        
        long count = permissionRepository.countByIdIn(permissionIds);
        if (count != permissionIds.size()) {
            throw new RuntimeException("Một hoặc nhiều Permission ID không tồn tại!");
        }
    }
}