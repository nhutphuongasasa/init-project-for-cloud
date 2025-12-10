package com.cloud.order_service.application.dto.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInboundItem {
    private UUID productVariantId;
    private Integer quantity;
}
