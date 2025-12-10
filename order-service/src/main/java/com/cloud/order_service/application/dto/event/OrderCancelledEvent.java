package com.cloud.order_service.application.dto.event;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OrderCancelledEvent {
    private UUID orderId;
    private String orderCode;
    private UUID vendorId;
    private UUID warehouseId;
    private List<ReserveItem> itemsToRelease;
}