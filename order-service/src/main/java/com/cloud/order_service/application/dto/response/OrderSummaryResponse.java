package com.cloud.order_service.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.cloud.order_service.domain.enums.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderSummaryResponse{
    private UUID id;
    private UUID vendorId;
    private String orderCode;
    private String customerName;
    private String customerPhone;
    private OrderStatus status;
    private String source;
    private UUID warehouseId;
    private Instant createdAt;
    private Integer totalItems;
    private Integer pickedItems;
    private BigDecimal totalAmount;
}