package com.cloud.inventory.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
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

    public Map<UUID, List<StockSummaryDto>> getStockByVariantIds(List<UUID> variantIds) {
        if (variantIds == null || variantIds.isEmpty()) {
            return Map.of();
        }

        UUID vendorId = jwtUtils.getCurrentUserId();

        // Tạo cache key unique dựa trên vendor + danh sách variantIds (sắp xếp để cùng input luôn cùng key)
        String variantIdsHash = variantIds.stream()
            .sorted()
            .map(UUID::toString)
            .collect(Collectors.joining(","));
        String cacheKey = "stock:summary:vendor:" + vendorId + ":variants:" + UUID.nameUUIDFromBytes(variantIdsHash.getBytes());

        RMapCache<String, Map<UUID, List<StockSummaryDto>>> cache = redisson.getMapCache("stock:variant:warehouse:summary");

        Map<UUID, List<StockSummaryDto>> cached = cache.get(cacheKey);
        if (cached != null) {
            log.debug("Cache HIT for stock by warehouse of {} variants", variantIds.size());
            return cached;
        }

        log.debug("Cache MISS - querying DB for stock by warehouse of {} variants", variantIds.size());

        List<InventoryItem> items = inventoryItemRepository.findByProductVariantIdInAndVendorId(variantIds, vendorId);

        Map<UUID, List<StockSummaryDto>> result = items.stream()
                .collect(Collectors.groupingBy(
                        InventoryItem::getProductVariantId,
                        Collectors.mapping(item -> StockSummaryDto.builder()
                                .warehouseId(item.getWarehouseId())
                                .quantityAvailable(item.getQuantityAvailable())
                                .build(),
                        Collectors.toList())
                ));

        variantIds.forEach(id -> result.putIfAbsent(id, List.of()));

        cache.fastPut(cacheKey, result, STOCK_CACHE_TTL.toMillis(), TimeUnit.MILLISECONDS);
        for (Map.Entry<UUID, List<StockSummaryDto>> entry : result.entrySet()) {
            UUID variantId = entry.getKey();
            List<StockSummaryDto> summaries = entry.getValue();
            log.info("Cached stock data for key={} | variantId={} | summaries={}", cacheKey, variantId, summaries);
        }
        return result;
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