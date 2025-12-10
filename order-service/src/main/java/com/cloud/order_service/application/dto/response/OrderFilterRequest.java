package com.cloud.order_service.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.cloud.order_service.domain.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFilterRequest{
    private List<OrderStatus> status;
    private UUID warehouseId;
    private String orderCode;
    private String customerPhone;
    private String externalRef;
    private Instant fromDate;
    private Instant toDate;
    private Integer page;
    private Integer size;
    private String sortBy;        // orderCode, createdAt...
    private String sortDir;        // asc, desc
}