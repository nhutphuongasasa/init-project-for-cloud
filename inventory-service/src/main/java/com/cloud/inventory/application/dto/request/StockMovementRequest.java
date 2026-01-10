package com.cloud.inventory.application.dto.request;

import java.util.UUID;

public interface StockMovementRequest {
    UUID getVendorId();
    UUID getProductVariantId();
    UUID getWarehouseId();
    Integer getQuantity();
}
