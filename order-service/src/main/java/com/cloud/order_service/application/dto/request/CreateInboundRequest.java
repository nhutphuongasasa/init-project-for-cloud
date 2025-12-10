package com.cloud.order_service.application.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInboundRequest {
    private UUID warehouseId;
    private String externalRef;
    private String supplierName;
    private LocalDate expectedAt;
    private List<InboundItemRequest> items;
}