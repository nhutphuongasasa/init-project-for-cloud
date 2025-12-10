package com.cloud.inventory.application.mapper;

import org.mapstruct.Mapper;

import com.cloud.inventory.application.dto.StockSummaryDto;
import com.cloud.inventory.application.dto.WarehouseStockDto;
import com.cloud.inventory.domain.model.InventoryItem;

@Mapper(componentModel = "spring")
public interface StockSummaryMapper {

    WarehouseStockDto toWarehouseStockDto(InventoryItem item);

    StockSummaryDto toSummaryDto(InventoryItem item);
}

