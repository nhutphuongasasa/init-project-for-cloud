package com.cloud.inventory.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class InventoryItemId implements Serializable {
    private UUID productVariantId;
    private UUID warehouseId;

    public InventoryItemId() {}

    public InventoryItemId(UUID productVariantId, UUID warehouseId) {
        this.productVariantId = productVariantId;
        this.warehouseId = warehouseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryItemId)) return false;
        InventoryItemId that = (InventoryItemId) o;
        return Objects.equals(productVariantId, that.productVariantId)
                && Objects.equals(warehouseId, that.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productVariantId, warehouseId);
    }
}
