package com.cloud.inventory.application.kafka.event;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInboundItem {
    private UUID productVariantId;
    private Integer quantityReceived;
}
