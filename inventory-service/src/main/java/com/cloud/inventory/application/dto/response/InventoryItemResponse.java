package com.cloud.inventory.application.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemResponse {
    private UUID productVariantId;
    private UUID warehouseId;
    private UUID vendorId;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private Integer safetyStock;
    private Instant lastUpdated;
}
