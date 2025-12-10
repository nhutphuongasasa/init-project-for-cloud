package com.cloud.inventory.application.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponse {
    private UUID id;
    private UUID productVariantId;
    private UUID warehouseId;
    private UUID vendorId;
    private String type;         
    private Integer quantity;
    // private String referenceType;
    // private String referenceId;
    private String notes;
    private String createdBy;
    private Instant createdAt;
}
