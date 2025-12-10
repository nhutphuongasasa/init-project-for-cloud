package com.cloud.inventory.application.kafka.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@ToString
public class OrderItemEvent implements Serializable {

    private final UUID productVariantId;
    private final UUID warehouseId;
    private final String sku;
    private final Integer quantity;
    private final UUID vendorId;

    @JsonCreator
    public OrderItemEvent(
            @JsonProperty("productVariantId") UUID productVariantId,
            @JsonProperty("warehouseId") UUID warehouseId,
            @JsonProperty("sku") String sku,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("vendorId") UUID vendorId) {
        this.productVariantId = productVariantId;
        this.warehouseId = warehouseId;
        this.sku = sku;
        this.quantity = quantity;
        this.vendorId = vendorId;
    }
}