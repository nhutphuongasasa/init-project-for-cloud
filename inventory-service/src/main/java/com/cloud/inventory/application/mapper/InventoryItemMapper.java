package com.cloud.inventory.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.cloud.inventory.application.dto.request.CreateInventoryItemRequest;
import com.cloud.inventory.domain.model.InventoryItem;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    InventoryItemMapper INSTANCE = Mappers.getMapper(InventoryItemMapper.class);

    @Mapping(target = "quantityReserved", constant = "0")
    @Mapping(target = "safetyStock", constant = "10")
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "quantityAvailable", constant = "0")
    InventoryItem toEntity(CreateInventoryItemRequest request, UUID vendorId);
}