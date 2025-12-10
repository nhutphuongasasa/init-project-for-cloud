package com.cloud.order_service.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InboundItemResponse {
    private Long id;
    private UUID productVariantId;
    // private String sku;
    private String productName;
    private Integer quantityExpected;
    private Integer quantityReceived;
    private BigDecimal unitPrice;
    private String notes;
}
