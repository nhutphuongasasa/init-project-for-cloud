package com.cloud.inventory.application.kafka.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuccessfulUpdateQuantityEvent {
    private String message;
    private UUID orderId;
    private String orderCode;
    private UUID warehouseId;
}