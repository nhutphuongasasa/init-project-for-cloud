package com.cloud.inventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor
public class StockReportDto {
    private long totalVariants;
    private int totalAvailable;
    private int totalReserved;
    private long lowStockVariants;
}