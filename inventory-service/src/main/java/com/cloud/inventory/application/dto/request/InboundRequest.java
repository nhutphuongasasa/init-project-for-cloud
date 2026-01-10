package com.cloud.inventory.application.dto.request;

import java.util.List;
import java.util.UUID;

import com.cloud.inventory.application.kafka.event.ReserveItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InboundRequest {
    private UUID orderId;
    private String orderCode;
    private UUID vendorId;
    private UUID warehouseId;
    private List<ReserveItem> items;
}
