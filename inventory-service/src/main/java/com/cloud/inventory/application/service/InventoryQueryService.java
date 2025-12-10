package com.cloud.inventory.application.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cloud.inventory.application.dto.*;
import com.cloud.inventory.application.dto.request.StockSearchRequest;
import com.cloud.inventory.application.dto.response.StockMovementResponse;
import com.cloud.inventory.application.mapper.StockMovementMapper;
import com.cloud.inventory.application.mapper.StockSummaryMapper;
import com.cloud.inventory.common.utils.jwt.JwtUtils;
import com.cloud.inventory.domain.model.InventoryItem;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.InventoryItemRepository;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.StockMovementRepository;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockSummaryMapper stockSummaryMapper;
    private final StockMovementMapper stockMovementMapper;
    private final JwtUtils jwtUtils;
    private final RedissonClient redisson;

    private static final Duration STOCK_CACHE_TTL     = Duration.ofMinutes(5);
    private static final Duration REPORT_CACHE_TTL    = Duration.ofMinutes(10);
    private static final Duration WAREHOUSE_CACHE_TTL = Duration.ofHours(24);

    public Map<UUID, StockSummaryDto> getStockByVariantIds(List<UUID> variantIds) {
        return getStockByVariantIds(variantIds, null);
    }

    public Map<UUID, StockSummaryDto> getStockByVariantIds(List<UUID> variantIds, List<UUID> warehouseIds) {
        if (variantIds == null || variantIds.isEmpty()) return Map.of();

        UUID vendorId = jwtUtils.getCurrentUserId();
        String cacheKey = "stock:summary:" + vendorId + ":" +
                (warehouseIds == null ? "all" : warehouseIds.stream()
                        .map(UUID::toString)
                        .collect(java.util.stream.Collectors.joining(",")));

        RMapCache<UUID, StockSummaryDto> cache = redisson.getMapCache(cacheKey);

        Map<UUID, StockSummaryDto> result = new HashMap<>(cache.getAll(new HashSet<>(variantIds)));

        Set<UUID> missing = variantIds.stream()
                .filter(id -> !result.containsKey(id))
                .collect(Collectors.toSet());

        if (!missing.isEmpty()) {
            Map<UUID, Integer> dbStock = inventoryItemRepository
                    .findByProductVariantIdInAndVendorIdAndWarehouseIdIn(
                            new ArrayList<>(missing), vendorId, warehouseIds)
                    .stream()
                    .collect(Collectors.groupingBy(
                        item -> item.getProductVariantId(),
                        Collectors.summingInt(InventoryItem::getQuantityAvailable)
                    ));

            missing.forEach(id -> {
                int qty = dbStock.getOrDefault(id, 0);
                StockSummaryDto dto = StockSummaryDto.builder()
                        .variantId(id)
                        .totalAvailable(qty)
                        .build();
                result.put(id, dto);
                cache.fastPut(id, dto, STOCK_CACHE_TTL.toMillis(), TimeUnit.MILLISECONDS);
            });
        }

        variantIds.forEach(id -> result.putIfAbsent(id,
                StockSummaryDto.builder().variantId(id).totalAvailable(0).build()));

        return Map.copyOf(result);
    }

    public StockDetailDto getStockDetail(UUID variantId) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        List<InventoryItem> items = inventoryItemRepository.findByProductVariantIdAndVendorId(variantId, vendorId);

        int totalAvailable = items.stream().mapToInt(InventoryItem::getQuantityAvailable).sum();
        int totalReserved = items.stream().mapToInt(InventoryItem::getQuantityReserved).sum();

        return StockDetailDto.builder()
                .variantId(variantId)
                .totalAvailable(totalAvailable)
                .totalReserved(totalReserved)
                .totalOnHand(totalAvailable + totalReserved)
                .warehouses(items.stream()
                        .map(stockSummaryMapper::toWarehouseStockDto)
                        .toList())
                .build();
    }

    public Page<StockSummaryDto> searchStock(StockSearchRequest request, Pageable pageable) {
        return inventoryItemRepository.searchStock(
            jwtUtils.getCurrentUserId(), request.getKeyword(), request.getWarehouseIds(),
            request.getMinStock(), request.getMaxStock(), pageable)
                .map(stockSummaryMapper::toSummaryDto);
    }

    public Page<StockMovementResponse> getMovementsByVariant(UUID variantId, Pageable pageable) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        return stockMovementRepository
                .findByProductVariantIdAndVendorId(variantId, vendorId, pageable)
                .map(stockMovementMapper::toResponse);
    }

    public StockReportDto getStockSummaryReport() {
        UUID vendorId = jwtUtils.getCurrentUserId();
        RMapCache<String, StockReportDto> cache = redisson.getMapCache("stock:reports");

        return Optional.ofNullable(cache.get(vendorId.toString()))
                .orElseGet(() -> {
                    List<InventoryItem> items = inventoryItemRepository.findByVendorId(vendorId);

                    long totalVariants = items.stream()
                            .map(item -> item.getProductVariantId())
                            .distinct()
                            .count();

                    int totalAvailable = items.stream().mapToInt(InventoryItem::getQuantityAvailable).sum();
                    int totalReserved = items.stream().mapToInt(InventoryItem::getQuantityReserved).sum();
                    long lowStockCount = items.stream()
                            .filter(i -> i.getQuantityAvailable() <= i.getSafetyStock())
                            .map(item -> item.getProductVariantId())
                            .distinct()
                            .count();

                    StockReportDto report = StockReportDto.builder()
                            .totalVariants(totalVariants)
                            .totalAvailable(totalAvailable)
                            .totalReserved(totalReserved)
                            .lowStockVariants(lowStockCount)
                            .build();

                    cache.fastPut(vendorId.toString(), report, REPORT_CACHE_TTL.toMillis(), TimeUnit.MILLISECONDS);
                    return report;
                });
    }

    public List<WarehouseDto> getMyWarehouses() {
        UUID vendorId = jwtUtils.getCurrentUserId();
        RList<WarehouseDto> cache = redisson.getList("vendor:warehouses:" + vendorId);

        if (!cache.isEmpty()) {
            return List.copyOf(cache);
        }

        List<WarehouseDto> warehouses = inventoryItemRepository.findDistinctWarehouseByVendorId(vendorId);
        if (!warehouses.isEmpty()) {
            cache.addAll(warehouses);
            cache.expire(WAREHOUSE_CACHE_TTL);
        }
        return warehouses;
    }
}