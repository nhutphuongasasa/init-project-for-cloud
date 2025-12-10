package com.cloud.inventory.application.kafka.event;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderShippedEvent {
    private UUID orderId;
    private String orderCode;
    private UUID warehouseId;
    private UUID vendorId;
    private List<OutboundItem> items;
}