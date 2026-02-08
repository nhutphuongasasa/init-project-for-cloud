package com.cloud.auth_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    
    private UUID id;

    private String code; // Ví dụ: "inventory:view"

    private String name; // Ví dụ: "Xem tồn kho"

    private String module; // Ví dụ: "INVENTORY"

    private String description;
}