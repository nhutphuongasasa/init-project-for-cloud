package com.cloud.inventory.application.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor
public class StockDetailDto {
    private UUID variantId;
    private int totalAvailable;
    private int totalReserved;
    private int totalOnHand;
    private List<WarehouseStockDto> warehouses;
}