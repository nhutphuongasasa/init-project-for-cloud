package com.cloud.inventory.application.mapper;

import java.time.Instant;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cloud.inventory.application.dto.request.CreateInventoryItemRequest;
import com.cloud.inventory.application.dto.request.CreateStockMovementRequest;
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
    @Mapping(target = "type", constant = "INBOUND")
    @Mapping(target = "quantity", source = "request.quantityAvailable")
    @Mapping(target = "notes", constant = "Nhập kho thủ công")
    @Mapping(target = "createdBy", expression = "java(\"vendor:\" + vendorId)")
    @Mapping(target = "createdAt", ignore = true)
    StockMovement toInboundMovement(CreateInventoryItemRequest request, UUID vendorId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "ADJUSTMENT")
    @Mapping(target = "createdAt", ignore = true)
    StockMovement toAdjustmentMovement(CreateStockMovementRequest request, UUID vendorId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productVariantId", source = "variantId")
    @Mapping(target = "warehouseId", source = "warehouseId")
    @Mapping(target = "vendorId", source = "vendorId")
    @Mapping(target = "type", constant = "TRANSFER")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "createdBy", expression = "java(\"vendor:\" + vendorId)")
    @Mapping(target = "createdAt", ignore = true)
    StockMovement toTransferOutMovement(UUID variantId, UUID warehouseId, UUID vendorId, int quantity, String notes);

    default StockMovement toTransferInMovement(UUID variantId, UUID warehouseId, UUID vendorId, int quantity, String notes) {
        return toTransferOutMovement(variantId, warehouseId, vendorId, quantity, notes); // giống nhau
    }

    StockMovementResponse toResponse(StockMovement movement);
}
