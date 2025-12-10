package com.cloud.order_service.application.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ApproveOrderRequest {
    private UUID vendorId;
    private UUID orderId;
}
