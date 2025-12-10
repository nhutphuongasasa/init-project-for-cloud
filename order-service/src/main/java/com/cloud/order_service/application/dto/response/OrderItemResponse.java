package com.cloud.order_service.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse{
    private Long id;
    private UUID productVariantId;
    // private String sku;
    private String productName;
    private Integer quantityRequested;
    private Integer quantityPicked;
    private BigDecimal unitPrice;
    private String notes;
}