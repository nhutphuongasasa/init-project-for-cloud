package com.cloud.inventory.application.dto.request;

import lombok.*;
// import jakarta.validation.constraints.*;
import java.util.UUID;

import com.cloud.inventory.domain.enums.ReferenceType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInboundRequest implements StockMovementRequest{

    // @NotNull(message = "Product variant ID is required")
    private UUID productVariantId;

    // @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    // @NotNull(message = "Vendor ID is required")
    private UUID vendorId;

    private ReferenceType type;

    private UUID orderId;

    private String orderCode;

    @Builder.Default
    // @Min(value = 0, message = "Quantity  must be non-negative")
    private Integer quantity = 0;

    // @Builder.Default
    // @Min(value = 0, message = "Safety stock must be non-negative")
    // private Integer safetyStock = 10;
}
