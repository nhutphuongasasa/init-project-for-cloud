package com.cloud.inventory.application.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

import com.cloud.inventory.domain.enums.StockMovementType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustStockRequest implements StockMovementRequest{

    @NotNull(message = "Product variant ID is required")
    private UUID productVariantId;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Vendor ID is required")
    private UUID vendorId;

    @NotNull(message = "Type is required")
    private StockMovementType type;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    private Integer delta;

    @NotBlank(message = "CreatedBy is required")
    private String createdBy;
}
