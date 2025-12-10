package com.cloud.order_service.application.dto.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundItemResponse {
    private Long id;
    private UUID productVariantId;
    private String productName;
    private Integer quantityExpected;
    private Integer quantityReceived;
    private String notes;
}