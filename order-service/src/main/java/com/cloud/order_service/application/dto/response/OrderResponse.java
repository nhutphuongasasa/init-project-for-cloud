package com.cloud.order_service.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.cloud.order_service.domain.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse{
    private UUID id;
    private String orderCode;
    private UUID vendorId;
    private UUID warehouseId;
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private String externalRef;
    private String source;
    private OrderStatus status;
    private Instant pickedAt;
    private Instant packedAt;
    private Instant shippedAt;
    private Instant cancelledAt;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private List<OrderItemResponse> items;
}

