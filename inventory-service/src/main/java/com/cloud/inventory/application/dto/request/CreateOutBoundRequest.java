package com.cloud.inventory.application.dto.request;

import lombok.*;

import java.util.UUID;

import com.cloud.inventory.domain.enums.ReferenceType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOutBoundRequest implements StockMovementRequest {
    private ReferenceType type;
    private UUID orderId;
    private String orderCode;
    private UUID vendorId;
    private UUID warehouseId;
    private Integer quantity;
    private UUID productVariantId;
}
