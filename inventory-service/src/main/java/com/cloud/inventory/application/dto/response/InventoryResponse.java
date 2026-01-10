package com.cloud.inventory.application.dto.response;

// import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private UUID id;
    private UUID productVariantId;
    private UUID warehouseId;
    private UUID vendorId;
    private String type;         
    private Integer quantityAvailable;
    // private String referenceType;
    // private String referenceId;
    // private String notes;
}
