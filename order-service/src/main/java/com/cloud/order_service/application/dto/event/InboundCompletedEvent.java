package com.cloud.order_service.application.dto.event;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InboundCompletedEvent {
    private UUID vendorId;
    private UUID orderInboundId;
    private String inboundCode;
    private UUID warehouseId;
    private List<InboundItemEvent> items;
}