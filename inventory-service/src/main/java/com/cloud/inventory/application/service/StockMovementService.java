package com.cloud.inventory.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.inventory.application.dto.request.StockMovementRecord;
import com.cloud.inventory.application.dto.response.StockMovementResponse;
import com.cloud.inventory.application.exception.custom.InsufficientStockException;
import com.cloud.inventory.application.exception.custom.InventoryNotFoundException;
import com.cloud.inventory.application.mapper.StockMovementMapper;
import com.cloud.inventory.common.utils.jwt.JwtUtils;
import com.cloud.inventory.domain.enums.StockMovementType;
import com.cloud.inventory.domain.model.InventoryItem;
import com.cloud.inventory.domain.model.StockMovement;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.InventoryItemRepository;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.StockMovementRepository;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementService {

    private final InventoryItemRepository inventoryRepo;
    private final StockMovementRepository movementRepo;
    private final StockMovementMapper movementMapper;
    private final RedissonClient redisson;
    private final JwtUtils jwtUtils;

    @Transactional
public StockMovementResponse recordMovement(StockMovementRecord record, UUID vendorId) {
    UUID adminId = jwtUtils.getCurrentUserId();
    String lockKey = "stock:movement:" + record.getProductVariantId() + ":" + record.getWarehouseId();
    log.info("stockmovementrecord {}", record);
    return withLock(lockKey, () -> {
        InventoryItem item = inventoryRepo
                .findByProductVariantIdAndWarehouseId(record.getProductVariantId(), record.getWarehouseId())
                .orElseGet(() -> {
                    if ("TRANSFER_IN".equals(record.getReferenceType())) {
                        InventoryItem newItem = new InventoryItem();
                        newItem.setProductVariantId(record.getProductVariantId());
                        newItem.setWarehouseId(record.getWarehouseId());
                        newItem.setVendorId(vendorId);
                        newItem.setQuantityAvailable(0);
                        newItem.setQuantityReserved(0);
                        newItem.setSafetyStock(10);
                        return inventoryRepo.save(newItem);
                    } else {
                        throw new InventoryNotFoundException();
                    }
                });

        switch (record.getType()) {
            case INBOUND, RETURN -> {
                // nhập kho hoặc hàng trả về
                item.setQuantityAvailable(item.getQuantityAvailable() + record.getQuantity());
            }
            case ADJUSTMENT -> {
                // điều chỉnh tồn kho
                int newQty = item.getQuantityAvailable() + record.getQuantity();
                if (newQty < 0) throw new InsufficientStockException("Out of stock");
                item.setQuantityAvailable(newQty);
            }
            case OUTBOUND -> {
                if ("ORDER_RESERVE".equals(record.getReferenceType())) {
                    // giữ chỗ cho đơn hàng
                    if (item.getQuantityAvailable() < record.getQuantity()) {
                        throw new InsufficientStockException("Not enough stock to reserve");
                    }
                    item.setQuantityReserved(item.getQuantityReserved() + record.getQuantity());
                    item.setQuantityAvailable(item.getQuantityAvailable() - record.getQuantity());
                } else if ("ORDER_RELEASE".equals(record.getReferenceType())) {
                    // hoàn tất đơn hàng
                    item.setQuantityReserved(item.getQuantityReserved() - record.getQuantity());
                    // available không tăng lại, vì hàng đã xuất kho
                } else {
                    throw new IllegalArgumentException("Unknown OUTBOUND referenceType");
                }
            }
            case TRANSFER -> {
                if ("TRANSFER_OUT".equals(record.getReferenceType())) {
                    // kho nguồn: giảm stock
                    int newQty = item.getQuantityAvailable() - record.getQuantity();
                    if (newQty < 0) throw new InsufficientStockException("Not enough stock to transfer");
                    item.setQuantityAvailable(newQty);
                } else if ("TRANSFER_IN".equals(record.getReferenceType())) {
                    // kho đích: tăng stock
                    item.setQuantityAvailable(item.getQuantityAvailable() + record.getQuantity());
                } else {
                    throw new IllegalArgumentException("Unknown TRANSFER referenceType");
                }
            }

        }

        inventoryRepo.save(item);

        StockMovement movement = StockMovement.builder()
                .productVariantId(record.getProductVariantId())
                .warehouseId(record.getWarehouseId())
                .vendorId(vendorId)
                .type(record.getType())
                .quantity(Math.abs(record.getQuantity()))
                .referenceType(record.getReferenceType())
                .notes(record.getNotes())
                .createdBy("admin:" + adminId)
                .createdAt(Instant.now())
                .build();

        StockMovement saved = movementRepo.save(movement);

        redisson.getKeys().deleteByPattern("stock:summary:" + vendorId + ":*");

        return movementMapper.toResponse(
                Objects.requireNonNull(saved, "Saved movement cannot be null")
        );
    });
}

    private <T> T withLock(String key, Supplier<T> supplier) {
        RLock lock = redisson.getLock(key);
        try {
            if (lock.tryLock(10, 60, TimeUnit.SECONDS)) {
                return supplier.get();
            } else {
                throw new RuntimeException("Unable to acquire inventory lock, please try again");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("System is busy, please try again later", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

// void recordMovement(StockMovementRecord record);
// List<StockMovementDto> getMovements(Long variantId, LocalDateTime from, LocalDateTime to);
// void recordBatch(List<StockMovementRecord> records); // dùng khi import Excel