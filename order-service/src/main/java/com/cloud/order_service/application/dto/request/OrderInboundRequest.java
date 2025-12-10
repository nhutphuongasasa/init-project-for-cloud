package com.cloud.order_service.application.dto.request;

import lombok.Getter;       
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderInboundRequest {
    private UUID orderInboundId;
    private String orderCode;
    private UUID warehouseId;
    private UUID vendorId;
    private List<OrderInboundItem> items;
}
