package com.cloud.order_service.application.dto.event;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OutboundItem {
    private UUID productVariantId;
    private Integer quantityPicked;
}