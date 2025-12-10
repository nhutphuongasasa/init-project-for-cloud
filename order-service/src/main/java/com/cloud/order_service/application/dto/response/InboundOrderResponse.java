package com.cloud.order_service.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.cloud.order_service.domain.enums.InboundStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InboundOrderResponse {
    private UUID id;
    private String inboundCode;
    private UUID vendorId;
    private UUID warehouseId;
    private String supplierName;
    private String externalRef;
    private Instant expectedAt;
    private InboundStatus status;
    private Instant receivedAt;
    private Instant createdAt;
    private UUID createdBy;
    private List<InboundItemResponse> items;
}