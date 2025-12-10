package com.cloud.order_service.application.dto.event;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InboundItemEvent {
    private UUID productVariantId;
    private Integer quantityReceived;
}
