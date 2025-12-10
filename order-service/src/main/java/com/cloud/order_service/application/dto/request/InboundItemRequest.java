package com.cloud.order_service.application.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundItemRequest {
    private UUID productVariantId;
    private String productName;
    private Integer quantityExpected;
    private BigDecimal unitPrice;
    private String notes;
}