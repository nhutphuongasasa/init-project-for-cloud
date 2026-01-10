package com.cloud.inventory.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.inventory.application.dto.request.AdjustStockRequest;
import com.cloud.inventory.application.dto.request.CreateInboundRequest;
import com.cloud.inventory.application.dto.request.CreateOutBoundRequest;
import com.cloud.inventory.application.dto.request.InboundRequest;
import com.cloud.inventory.application.dto.request.OrphanCheckResult;
import com.cloud.inventory.application.dto.response.InventoryResponse;
import com.cloud.inventory.application.exception.custom.InsufficientStockException;
import com.cloud.inventory.application.exception.custom.InventoryNotFoundException;
import com.cloud.inventory.application.exception.custom.WarehouseNotFoundException;
import com.cloud.inventory.application.kafka.event.FailedUpdateQuantityEvent;
import com.cloud.inventory.application.kafka.event.OrderCancelledEvent;
import com.cloud.inventory.application.kafka.event.OrderStockEvent;
import com.cloud.inventory.application.kafka.event.ReserveItem;
import com.cloud.inventory.application.kafka.event.SuccessfulUpdateQuantityEvent;
import com.cloud.inventory.application.mapper.InventoryItemMapper;
import com.cloud.inventory.application.mapper.RequestMapper;
import com.cloud.inventory.common.utils.jwt.JwtUtils;
import com.cloud.inventory.domain.enums.FailedUpdateQuantityInventory;
import com.cloud.inventory.domain.enums.ReferenceType;
import com.cloud.inventory.domain.enums.StockMovementType;
import com.cloud.inventory.domain.model.InventoryItem;
import com.cloud.inventory.domain.model.Warehouse;
import com.cloud.inventory.infrastructure.adapter.inbound.mq.publisher.InventoryKafkaPublisher;
import com.cloud.inventory.infrastructure.adapter.outbound.client.OrderClient;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.InventoryItemRepository;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCommandService {

    private final InventoryItemRepository inventoryRepo;
    private final InventoryItemMapper itemMapper;
    private final RedissonClient redisson;
    private final StockMovementService stockMovementService;
    private final JwtUtils jwtUtils;
    private final OrderClient orderClient;
    private final InventoryKafkaPublisher publisher;
    private final WarehouseRepository warehouseRepository; 
    private final RequestMapper requestMapper;

    public void cancelOrderOutBound(OrderCancelledEvent request, ReferenceType type){
        List<ReserveItem> sortedItems = sortedReserveItems(request.getItemsToRelease());

        sortAndMultiLock(
            sortedItems,
            request.getWarehouseId(),
            () -> executeInCancelOrderTransaction(sortedItems, request, type),
            request.getOrderId()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    private void executeInCancelOrderTransaction(List<ReserveItem> sortedItems, OrderCancelledEvent request, ReferenceType type) {
        sortedItems.forEach(item -> {
            CreateOutBoundRequest req = CreateOutBoundRequest.builder()
                .type(type)
                .orderId(request.getOrderId())
                .orderCode(request.getOrderCode())
                .warehouseId(request.getWarehouseId())
                .vendorId(request.getVendorId())
                .productVariantId(item.getProductVariantId())
                .quantity(item.getQuantity())
                .build();

            releaseReservedStock(req);
        });
    }

    private void releaseReservedStock(CreateOutBoundRequest request) {
        InventoryItem item = findProductInventoryItem(request.getProductVariantId(), request.getWarehouseId());

        if (item == null) {
            log.warn("InventoryItem not found during release for variant: {}", request.getProductVariantId());
            return; // hoặc throw tùy yêu cầu
        }

        Integer beforeQuantity = item.getQuantityAvailable();

        item.setQuantityReserved(item.getQuantityReserved() - request.getQuantity());
        item.setQuantityAvailable(item.getQuantityAvailable() + request.getQuantity()); // <<< QUAN TRỌNG: CỘNG LẠI AVAILABLE!!!

        stockMovementService.recordOutBoundReleaseStock(request, beforeQuantity);

        inventoryRepo.save(item);

        publisher.cancelledOrder(
            SuccessfulUpdateQuantityEvent.builder()
                .orderId(request.getOrderId())
                .orderCode(request.getOrderCode())
                .warehouseId(request.getWarehouseId())
                .build()
        );
    }

    public void createOutBound(OrderStockEvent request, ReferenceType type){
        List<ReserveItem> sortedItems = sortedReserveItems(request.getItems());

        sortAndMultiLock(
            sortedItems, 
            request.getWarehouseId(),
            () -> executeInOutboundTransaction(sortedItems, request, type), 
            request.getOrderId()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    private void executeInOutboundTransaction(List<ReserveItem> sortedItems, OrderStockEvent request, ReferenceType type) {
        sortedItems.forEach(item -> {
            CreateOutBoundRequest req = requestMapper.toCreateOutBoundRequest(request, type, item.getProductVariantId(), item.getQuantity());
            addOutbound(req);
        });
    }

    private List<ReserveItem> sortedReserveItems(List<ReserveItem> listItems){
        if (listItems == null || listItems.isEmpty()) {
            return List.of();
        }
        return listItems.stream()
            .sorted(Comparator.comparing(item -> item.getProductVariantId().toString()))
            .toList();
    }

    private void addOutbound(CreateOutBoundRequest request){
        InventoryItem item = findProductInventoryItem(request.getProductVariantId(), request.getWarehouseId());

        if(item == null){
            log.warn("InventoryItem not found for productVariantId={} and warehouseId={}. " +
                "The product may not have been stocked yet or the warehouse ID may be incorrect.",
                request.getProductVariantId(), request.getWarehouseId());

            FailedUpdateQuantityEvent failEvent = buildFailedEvent(request, FailedUpdateQuantityInventory.PRODUCT_NOT_IN_WAREHOUSE, "Product not found", null);

            publisher.productNotInWarehouse(failEvent);

            throw new InventoryNotFoundException(request.getProductVariantId().toString());
        }

        ReferenceType referenceType = request.getType();

        Integer beforeQuantity = item.getQuantityAvailable();

        if (referenceType == ReferenceType.ORDER_RESERVE){

            if(item.getQuantityAvailable() < request.getQuantity()) {
                publisher.failedInsufficientStock(null);
                throw new InsufficientStockException("Not enough stock to reserve");
            }
            item.setQuantityReserved(item.getQuantityReserved() + request.getQuantity());
            item.setQuantityAvailable(item.getQuantityAvailable() - request.getQuantity());

            stockMovementService.recordOutBoundReserveStock(request, beforeQuantity);

            inventoryRepo.save(item);

            publisher.reservedOrderOutbound(
                SuccessfulUpdateQuantityEvent.builder()
                    .orderId(request.getOrderId())
                    .orderCode(request.getOrderCode())
                    .warehouseId(request.getWarehouseId())
                    .build()
            );
        }else if (referenceType == ReferenceType.ORDER_RELEASE){

            item.setQuantityReserved(item.getQuantityReserved() - request.getQuantity());
            stockMovementService.recordOutBoundReleaseStock(request, beforeQuantity);

            inventoryRepo.save(item);

            publisher.releaseOrderOutbound(
                SuccessfulUpdateQuantityEvent.builder()
                    .orderId(request.getOrderId())
                    .orderCode(request.getOrderCode())
                    .warehouseId(request.getWarehouseId())
                    .build()
            );
        }

        log.warn("Unsupported ReferenceType: {} for outbound", referenceType);
        log.info("OUTBOUND not reserve or release");
        throw new IllegalArgumentException("Unsupported reference type: " + referenceType);

    }

    public void createInbound(InboundRequest request, ReferenceType type) {
        List<ReserveItem> sortedItems = sortedReserveItems(request.getItems());

        sortAndMultiLock(
            sortedItems,
            request.getWarehouseId(),
            () -> executeInInboundTransaction(sortedItems, request, type),
            request.getOrderId()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    private void executeInInboundTransaction(List<ReserveItem> sortedItems, InboundRequest request, ReferenceType type) {
        sortedItems.forEach(item -> {
            CreateInboundRequest req = requestMapper.toCreateInboundRequest(request, type, item.getProductVariantId(), item.getQuantity());
            addInbound(req);
        });
    }

    private void addInbound(CreateInboundRequest request){
        InventoryItem item = findProductInventoryItem(request.getProductVariantId(), request.getWarehouseId());

        ReferenceType type = request.getType();

        if(item == null && type == ReferenceType.RETURN_PRODUCT){
            log.error("InventoryItem not found for productVariantId={} and warehouseId={}. " +
                "The product may not have been stocked yet or the warehouse ID may be incorrect.",
                request.getProductVariantId(), request.getWarehouseId());
            throw new InventoryNotFoundException(request.getProductVariantId().toString());
        } else if (item == null && type == ReferenceType.INBOUND_ORDER){
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new WarehouseNotFoundException());

            item = inventoryRepo.save(
                Objects.requireNonNull(itemMapper.toEntity(request, warehouse))
            );
        }else if(item == null){
            throw new WarehouseNotFoundException();
        }
        
        item.setQuantityAvailable(item.getQuantityAvailable() + request.getQuantity());

        inventoryRepo.save(item);
        
        UUID performedBy = jwtUtils.getCurrentUserId();

        if(type == ReferenceType.INBOUND_ORDER){
            stockMovementService.recordCreateInbound(request, performedBy, item.getQuantityAvailable());
        } else if(type == ReferenceType.RETURN_PRODUCT){
            stockMovementService.recordReturnProductInventory(request, performedBy, item.getQuantityAvailable());
        }
    }

    //use for check real quantity compare quantity in db
    public InventoryResponse adjustStock(AdjustStockRequest request) {
        String lockKey = "stock:" + request.getProductVariantId() + ":" + request.getWarehouseId();
        return withLock(lockKey, () -> {
            log.info("Receive AdjustStock request for ProductVariant: {} at Warehouse: {}", 
                request.getProductVariantId(), request.getWarehouseId());
            
            InventoryItem item = findProductInventoryItem(request.getProductVariantId(), request.getWarehouseId());

            if(item == null){
                throw new InventoryNotFoundException(request.getProductVariantId().toString());
            }

            UUID performedId = jwtUtils.getCurrentUserId();

            Integer quantityInDB = item.getQuantityAvailable();

            if(quantityInDB != request.getQuantity()){
                item.setQuantityAvailable(request.getQuantity());
                Integer delta = request.getQuantity() - quantityInDB;
                
                log.debug("Adjusting stock: CurrentInDB={}, Target={}, Delta={}", 
                    quantityInDB, request.getQuantity(), delta);
                
                request.setDelta(delta);
            }else{
                request.setDelta(0);
            }

            stockMovementService.recordAdjustStock(request, performedId, quantityInDB);

            return itemMapper.toResponse(item, StockMovementType.ADJUSTMENT);
        });
    }

    //clean reserved inventory
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOrphanReserved(){
        log.info("=== BẮT ĐẦU CLEANUP ORPHAN RESERVED ===");

        int offset = 0;
        int size = 250;
        boolean hasMoreData = true;

        Pageable request = PageRequest.of(offset, size);

        while (hasMoreData) {
            offset++;

            Page<InventoryItem> items = inventoryRepo.findByQuantityReservedGreaterThanWithLock(0, request);

            List<OrphanCheckResult> batch = items.getContent()
                .stream()
                .map( item -> {
                    return OrphanCheckResult.builder()
                    .productVariantId(item.getProductVariantId())
                    .warehouseId(item.getWarehouseId())
                    .build();
                })
                .toList();

            List<OrphanCheckResult> orphanReservedList = null;

            try {
                orphanReservedList = orderClient.pushCheckListOrphanReserved(batch);
            } catch (Exception e) {
                log.error("Error when calling order service in page {}: {}", offset, e.getMessage(), e);
                continue;
            }

            if(orphanReservedList != null && !orphanReservedList.isEmpty()){
                orphanReservedList.forEach(orphan -> {
                    resetReserved(orphan);
                });
            } else if(orphanReservedList != null && orphanReservedList.size() != 500){
                hasMoreData = false;
            }
        }

        log.info("=== CLEANUP ORPHAN RESERVED HOÀN TẤT ===");
    }

    private void sortAndMultiLock(List<ReserveItem> sortedItems, UUID warehouseId, Runnable lockedAction, UUID orderId){
        List<RLock> locks = sortedItems.stream()
            .map(item -> redisson.getLock("stock:" + item.getProductVariantId() + ":" + warehouseId))
            .collect(Collectors.toList());

        RLock multiLock = redisson.getMultiLock(locks.toArray(new RLock[0]));

        try {
            boolean isLocked = multiLock.tryLock(10, 30, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    log.info("Successfully acquired lock for order: {}", orderId);
                    lockedAction.run();
                } finally {
                    multiLock.unlock();
                    log.info("Released lock for order: {}", orderId);
                }
            } else {
                throw new RuntimeException("System is busy, unable to acquire warehouse lock at this time!");
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("System error occurred while waiting for lock");
        }
    }

    private FailedUpdateQuantityEvent buildFailedEvent(
        CreateOutBoundRequest request, 
        FailedUpdateQuantityInventory error, 
        String message,
        Integer availableQuantity
    ) {
        
        return FailedUpdateQuantityEvent.builder()
                .orderId(request.getOrderId())
                .productVariantId(request.getProductVariantId())
                .requestedQuantity(request.getQuantity())
                .availableQuantity(availableQuantity == null ? 0 : availableQuantity)
                .errorCode(error.toString())
                .errorMessage(message)
                .build();
    }

    private void resetReserved(OrphanCheckResult orphan){
        String lockKey = "stock:" + orphan.getProductVariantId() + ":" + orphan.getWarehouseId();
        withLockNoReturn(lockKey, () -> {
            try {
                InventoryItem item = inventoryRepo.findByProductVariantIdAndWarehouseId(orphan.getProductVariantId(), orphan.getWarehouseId())
                    .orElse(null);

                if(item != null && item.getQuantityReserved() <= 50){
                    item.setQuantityAvailable(
                        item.getQuantityAvailable() + item.getQuantityReserved()
                    );
                    item.setQuantityReserved(0);
                } else if(item != null && item.getQuantityReserved() > 50){
                }
            } catch (Exception e) {
                log.error("Error in resetReserved with product variantId = [ID = {}] and warehouseId = [ID = {}]", orphan.getProductVariantId(), orphan.getWarehouseId());
            } 
        });
    }

    private InventoryItem findProductInventoryItem(UUID id, UUID warehouseId){
        return inventoryRepo.findByProductVariantIdAndWarehouseId(id, warehouseId)
            .orElse(null);
    }

    private <T> T withLock(String key, Supplier<T> supplier) {
        RLock lock = redisson.getLock(key);
        try {
            lock.lock(60, TimeUnit.SECONDS);
            return supplier.get();
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    private void withLockNoReturn(String key, Runnable action) {
        RLock lock = redisson.getLock(key);
        try {
            lock.lock(60, TimeUnit.SECONDS);
            action.run();
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}