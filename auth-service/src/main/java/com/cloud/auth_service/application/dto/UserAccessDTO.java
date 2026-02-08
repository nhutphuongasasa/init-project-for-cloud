package com.cloud.auth_service.application.dto;

import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAccessDTO {
    private UUID userId;
    private UUID vendorId;
    private String roleCode;
    private Set<String> permissions;
}