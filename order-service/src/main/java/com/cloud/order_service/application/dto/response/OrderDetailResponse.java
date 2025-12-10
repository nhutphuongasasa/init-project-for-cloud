package com.cloud.order_service.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class OrderDetailResponse {
    private Long id;
    private UUID productVariantId;
    private String productName;
    private Integer quantityRequested;
    private Integer quantityPicked;
    private BigDecimal unitPrice;
    private String notes;
}
