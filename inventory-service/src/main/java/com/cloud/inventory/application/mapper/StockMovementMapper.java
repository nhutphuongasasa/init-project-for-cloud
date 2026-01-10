package com.cloud.inventory.application.mapper;

import java.time.Instant;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cloud.inventory.application.dto.response.StockMovementResponse;
import com.cloud.inventory.domain.enums.StockMovementType;
import com.cloud.inventory.domain.model.StockMovement;

@Mapper(
    componentModel = "spring", 
    imports = {
        Instant.class, 
        StockMovementType.class
    })
public interface StockMovementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productVariantId", source = "variantId")
    @Mapping(target = "warehouseId", source = "warehouseId")
    @Mapping(target = "vendorId", source = "vendorId")
    @Mapping(target = "type", constant = "TRANSFER")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "createdBy", expression = "java(\"vendor:\" + vendorId)")
    @Mapping(target = "createdAt", ignore = true)
    StockMovement toTransferOutMovement(UUID variantId, UUID warehouseId, UUID vendorId, int quantity, String notes);

    default StockMovement toTransferInMovement(UUID variantId, UUID warehouseId, UUID vendorId, int quantity, String notes) {
        return toTransferOutMovement(variantId, warehouseId, vendorId, quantity, notes);
    }

    StockMovementResponse toResponse(StockMovement movement);
}
