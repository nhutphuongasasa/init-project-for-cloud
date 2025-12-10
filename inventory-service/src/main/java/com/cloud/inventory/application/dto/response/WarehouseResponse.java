package com.cloud.inventory.application.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponse {
    private UUID id;
    private String code;
    private String name;
    private String address;
    private Boolean isActive;
    private Instant createdAt;
}
