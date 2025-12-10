package com.cloud.inventory.application.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInventoryItemRequest {

    @NotNull(message = "Product variant ID is required")
    private UUID productVariantId;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Vendor ID is required")
    private UUID vendorId;

    @Builder.Default
    @Min(value = 0, message = "Quantity available must be non-negative")
    private Integer quantityAvailable = 0;

    @Builder.Default
    @Min(value = 0, message = "Quantity reserved must be non-negative")
    private Integer quantityReserved = 0;

    @Builder.Default
    @Min(value = 0, message = "Safety stock must be non-negative")
    private Integer safetyStock = 10;
}
