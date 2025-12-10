package com.cloud.inventory.application.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter 
@Setter 
@Builder
public class StockSearchRequest {
    private String keyword;        // SKU, tên sản phẩm
    private List<UUID> warehouseIds;
    private Integer minStock;
    private Integer maxStock;
    private Boolean lowStockOnly;
}