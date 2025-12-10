package com.cloud.order_service.application.dto.event;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OrderApprovedEvent {
    private UUID orderId;
    private String orderCode;
    private UUID warehouseId;
    private UUID vendorId;
    private List<ReserveItem> items;
}