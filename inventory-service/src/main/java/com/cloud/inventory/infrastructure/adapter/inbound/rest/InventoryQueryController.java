package com.cloud.inventory.infrastructure.adapter.inbound.rest;

import com.cloud.inventory.application.dto.*;
import com.cloud.inventory.application.dto.request.StockSearchRequest;
import com.cloud.inventory.application.dto.response.StockMovementResponse;
import com.cloud.inventory.application.service.InventoryQueryService;
import com.cloud.inventory.common.response.FormResponse;
import com.cloud.inventory.common.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory-query")
public class InventoryQueryController {

    private final InventoryQueryService queryService;

    @GetMapping("/stock")
    public FormResponse<Map<UUID, StockSummaryDto>> getStockByVariantIds(
            @RequestParam("ids") List<UUID> variantIds) {

        Map<UUID, StockSummaryDto> stock = queryService.getStockByVariantIds(variantIds);
        return FormResponse.<Map<UUID, StockSummaryDto>>builder()
                .data(stock)
                .message("Stock retrieved successfully")
                .timestamp(Instant.now())
                .build();
    }

    @GetMapping("/stock/{variantId}")
    public FormResponse<StockDetailDto> getStockDetail(@PathVariable UUID variantId) {
        StockDetailDto detail = queryService.getStockDetail(variantId);
        return FormResponse.<StockDetailDto>builder()
                .data(detail)
                .message("Stock detail retrieved successfully")
                .timestamp(Instant.now())
                .build();
    }

    @GetMapping("/search")
    public PageResponse<StockSummaryDto> searchStock(
            @Valid StockSearchRequest request,
            @PageableDefault(size = 20, sort = "lastUpdated,desc") Pageable pageable) {

        Page<StockSummaryDto> page = queryService.searchStock(request, pageable);
        return PageResponse.from(page)
                .message("Stock search completed successfully")
                .timestamp(Instant.now())
                .build();
    }

    @GetMapping("/movements/{variantId}")
    public PageResponse<StockMovementResponse> getMovements(
            @PathVariable UUID variantId,
            @PageableDefault(size = 50, sort = "createdAt,desc") Pageable pageable) {

        Page<StockMovementResponse> page = queryService.getMovementsByVariant(variantId, pageable);
        return PageResponse.<StockMovementResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @GetMapping("/report")
    public FormResponse<StockReportDto> getStockReport() {
        StockReportDto report = queryService.getStockSummaryReport();
        return FormResponse.<StockReportDto>builder()
                .data(report)
                .message("Stock report retrieved successfully")
                .timestamp(Instant.now())
                .build();
    }

    @GetMapping("/warehouses")
    public FormResponse<List<WarehouseDto>> getMyWarehouses() {
        List<WarehouseDto> warehouses = queryService.getMyWarehouses();
        return FormResponse.<List<WarehouseDto>>builder()
                .data(warehouses)
                .message("Warehouse list retrieved successfully")
                .timestamp(Instant.now())
                .build();
    }
}
