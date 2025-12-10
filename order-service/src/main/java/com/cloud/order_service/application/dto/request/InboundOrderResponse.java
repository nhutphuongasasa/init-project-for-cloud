package com.cloud.order_service.application.dto.request;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InboundOrderResponse {
    private UUID id;
    private String inboundCode;
    private UUID warehouseId;
    private String supplierName;
    private LocalDate expectedAt;
    private String status;
    private Instant receivedAt;
    private List<InboundItemResponse> items;
}