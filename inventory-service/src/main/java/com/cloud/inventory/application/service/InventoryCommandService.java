package com.cloud.inventory.application.service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.inventory.application.dto.request.CreateInventoryItemRequest;
import com.cloud.inventory.application.dto.request.CreateStockMovementRequest;
import com.cloud.inventory.application.dto.request.StockMovementRecord;
import com.cloud.inventory.application.dto.request.TransferRequest;
import com.cloud.inventory.application.dto.response.StockMovementResponse;
import com.cloud.inventory.application.mapper.InventoryItemMapper;
import com.cloud.inventory.domain.enums.StockMovementType;
import com.cloud.inventory.domain.model.InventoryItem;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.InventoryItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryCommandService {

    private final InventoryItemRepository inventoryRepo;
    private final InventoryItemMapper itemMapper;
    private final RedissonClient redisson;
    private final StockMovementService stockMovementService;

    public StockMovementResponse createInbound(CreateInventoryItemRequest req, UUID vendorId) {
        String lockKey = "stock:" + req.getProductVariantId() + ":" + req.getWarehouseId();
        return withLock(lockKey, () -> {
            InventoryItem item = inventoryRepo
                .findByProductVariantIdAndWarehouseId(req.getProductVariantId(), req.getWarehouseId())
                .orElseGet(() -> inventoryRepo.save(
                    Objects.requireNonNull(itemMapper.toEntity(req, vendorId), "Mapped entity cannot be null")
                ));

            StockMovementRecord record = StockMovementRecord.builder()
                .productVariantId(req.getProductVariantId())
                .warehouseId(req.getWarehouseId())
                .type(StockMovementType.INBOUND)
                .quantity(req.getQuantityAvailable())
                .referenceType("INBOUND")
                .notes("Inbound stock created")
                .build();

            return stockMovementService.recordMovement(record, vendorId);
        });
    }
    
    public StockMovementResponse adjustStock(CreateStockMovementRequest req, UUID vendorId) {
        String lockKey = "stock:" + req.getProductVariantId() + ":" + req.getWarehouseId();
        return withLock(lockKey, () -> {
            // InventoryItem item = inventoryRepo
            //     .findByProductVariantIdAndWarehouseId(req.getProductVariantId(), req.getWarehouseId())
            //     .orElseThrow(() -> new InventoryNotFoundException());

             StockMovementRecord record = StockMovementRecord.builder()
                .productVariantId(req.getProductVariantId())
                .warehouseId(req.getWarehouseId())
                .type(req.getType())
                .quantity(req.getQuantity())
                .referenceType(req.getReferenceType())
                .notes(req.getNotes())
                .build();

            return stockMovementService.recordMovement(record, vendorId);
        });
    }




    public String transferStock(TransferRequest req, UUID vendorId) {
        String lockKey = "stock:variant:" + req.getVariantId();

        return withLock(lockKey, () -> {
            UUID variantId = UUID.fromString(req.getVariantId());
            UUID fromWh = UUID.fromString(req.getFromWarehouseId());
            UUID toWh = UUID.fromString(req.getToWarehouseId());
            int qty = req.getQuantity();

            if (qty <= 0) {
                throw new IllegalArgumentException("Quantity to transfer must be greater than 0");
            }

            StockMovementRecord outbound = StockMovementRecord.builder()
                .productVariantId(variantId)
                .warehouseId(fromWh)
                .type(StockMovementType.TRANSFER)
                .quantity(qty)
                .referenceType("TRANSFER_OUT")
                .notes("Transfer to " + toWh + 
                    (req.getNotes() != null ? ". " + req.getNotes() : ""))
                .build();

            stockMovementService.recordMovement(outbound, vendorId);

            StockMovementRecord inbound = StockMovementRecord.builder()
                .productVariantId(variantId)
                .warehouseId(toWh)
                .type(StockMovementType.TRANSFER)
                .quantity(qty)
                .referenceType("TRANSFER_IN")
                .notes("Received transfer from " + fromWh + 
                    (req.getNotes() != null ? ". " + req.getNotes() : ""))
                .build();

            stockMovementService.recordMovement(inbound, vendorId);

            return "Successfully transferred " + qty + " items from warehouse " +
                req.getFromWarehouseId() + " to " + req.getToWarehouseId();
        });
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

    private void withLock(String key, Runnable action) {
        withLock(key, () -> { action.run(); return null; });
    }
}