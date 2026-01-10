package com.cloud.inventory.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.cloud.inventory.application.dto.request.CreateInboundRequest;
import com.cloud.inventory.application.dto.request.CreateOutBoundRequest;
import com.cloud.inventory.application.dto.request.InboundRequest;
import com.cloud.inventory.application.kafka.event.OrderStockEvent;
import com.cloud.inventory.domain.enums.ReferenceType;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    CreateOutBoundRequest toCreateOutBoundRequest(OrderStockEvent request, ReferenceType type, UUID productVariantId, Integer quantity);
    CreateInboundRequest toCreateInboundRequest(InboundRequest request, ReferenceType type, UUID productVariantId, Integer quantity);
}
