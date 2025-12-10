package com.cloud.order_service.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CancelOrderRequest{
    private String reason;
    private UUID orderId;
    private UUID vendorId;
}