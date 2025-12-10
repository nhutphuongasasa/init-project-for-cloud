package com.cloud.order_service.application.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest{
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private String externalRef;           // mã đơn sàn (nếu có)
    private String source;                // MANUAL, SHOPEE, LAZADA, ZALO...
    private UUID warehouseId;
    private List<CreateOrderItemRequest> items;
}