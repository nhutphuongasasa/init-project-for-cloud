package com.cloud.order_service.application.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInboundItemRequest {
    private UUID productVariantId;
    private String sku;
    private String productName;
    private Integer quantityExpected;
    private BigDecimal unitPrice;
    private String notes;
}