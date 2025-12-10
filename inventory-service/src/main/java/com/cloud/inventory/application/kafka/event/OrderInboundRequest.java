package com.cloud.inventory.application.kafka.event;

import lombok.Getter;       
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderInboundRequest {
    private UUID vendorId;
    private UUID orderInboundId;
    private String inboundCode;
    private UUID warehouseId;
    private List<OrderInboundItem> items;
}
