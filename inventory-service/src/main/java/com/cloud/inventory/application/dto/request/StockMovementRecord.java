package com.cloud.inventory.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import com.cloud.inventory.domain.enums.StockMovementType;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StockMovementRecord {

    @NotNull private UUID productVariantId;
    @NotNull private UUID warehouseId;

    @NotNull private StockMovementType type;         

    @PositiveOrZero private Integer quantity;          

    private String referenceType;
    // private String referenceId;     

    private String notes;
}