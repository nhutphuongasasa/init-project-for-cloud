package com.cloud.inventory.application.kafka.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboundItem {
    private UUID productVariantId;
    private Integer quantityPicked;
}