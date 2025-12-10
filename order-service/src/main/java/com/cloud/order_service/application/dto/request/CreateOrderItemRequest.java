package com.cloud.order_service.application.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderItemRequest{
    private UUID productVariantId;
    // private String sku;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String notes;
}