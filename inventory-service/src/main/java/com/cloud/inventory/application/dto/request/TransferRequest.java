package com.cloud.inventory.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    @NotBlank(message = "Variant ID is required")
    private String variantId;

    @NotBlank(message = "Source warehouse is required")
    private String fromWarehouseId;

    @NotBlank(message = "Destination warehouse is required")
    private String toWarehouseId;

    @NotNull(message = "Transfer quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @Max(value = 10000, message = "Maximum quantity is 10,000")
    private Integer quantity;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
