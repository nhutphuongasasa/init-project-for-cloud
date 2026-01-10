package com.cloud.inventory.application.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cloud.inventory.application.dto.request.CreateInboundRequest;
import com.cloud.inventory.application.dto.response.InventoryResponse;
import com.cloud.inventory.domain.enums.StockMovementType;
import com.cloud.inventory.domain.model.InventoryItem;
import com.cloud.inventory.domain.model.Warehouse;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {


    InventoryResponse toResponse(InventoryItem item, StockMovementType type);


    @Mapping(target = "quantityReserved", constant = "0")
    @Mapping(target = "safetyStock", constant = "10")
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "quantityAvailable", constant = "0")
    InventoryItem toEntity(CreateInboundRequest request, Warehouse warehouse);
}