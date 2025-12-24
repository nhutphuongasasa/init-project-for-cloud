package com.cloud.inventory.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor
@ToString
public class StockSummaryDto {
    private UUID warehouseId;
    private Integer quantityAvailable;
}