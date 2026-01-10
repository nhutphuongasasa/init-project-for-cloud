package com.cloud.inventory.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnProductRequest implements StockMovementRequest{
    @NotBlank(message = "productVariantId can not be null")
    private UUID productVariantId;

    @NotBlank(message = "warehouseId can not be null")
    private UUID warehouseId;

    @NotBlank(message = "quantity can not be null")
    private Integer quantity;

    @NotBlank(message = "vendorId can not be null")
    private UUID vendorId;
}
