package com.cloud.inventory.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor
public class WarehouseStockDto {
    private UUID warehouseId;
    private String warehouseName;
    private int quantityAvailable;
    private int quantityReserved;
}