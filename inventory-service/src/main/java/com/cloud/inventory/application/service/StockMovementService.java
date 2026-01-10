package com.cloud.inventory.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.cloud.inventory.application.dto.request.AdjustStockRequest;
import com.cloud.inventory.application.dto.request.CreateInboundRequest;
import com.cloud.inventory.application.dto.request.CreateOutBoundRequest;
import com.cloud.inventory.application.dto.request.StockMovementRequest;
import com.cloud.inventory.domain.enums.ReferenceType;
import com.cloud.inventory.domain.enums.StockMovementType;
import com.cloud.inventory.domain.model.StockMovement;
import com.cloud.inventory.infrastructure.adapter.outbound.repository.StockMovementRepository;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMovementService {

    // private final InventoryItemRepository inventoryRepo;
    private final StockMovementRepository movementRepo;
    // private final StockMovementMapper movementMapper;
    // private final RedissonClient redisson;
    // private final JwtUtils jwtUtils;

    public void recordCreateInbound(CreateInboundRequest request, UUID performedBy, Integer beforeQuantity){
        log.info("Recording return for product variant: {}", request.getProductVariantId());
        
        log.debug("Return detail: Vendor={}, Qty={}, Warehouse={}", request.getVendorId(), request.getQuantity(), request.getWarehouseId());
        
        StockMovementType type = StockMovementType.INBOUND;

        movementRepo.save(
            Objects.requireNonNull(
                buildStockMovement(type, beforeQuantity, null, request.getVendorId(), request)
            )
        );

        log.info("Inventory inbound completed for vendorId: {}", request.getVendorId());                
    }
    

    public void recordReturnProductInventory(CreateInboundRequest request, UUID performedBy, Integer beforeQuantity){
        log.info("Recording return for product variant: {}", request.getProductVariantId());

        log.debug("Return detail: Vendor={}, Qty={}, Warehouse={}", request.getVendorId(), request.getQuantity(), request.getWarehouseId());
        
        StockMovementType type = StockMovementType.RETURN;
        
        movementRepo.save(Objects.requireNonNull(
            buildStockMovement(type, beforeQuantity, null, performedBy, request)
        ));

        // log.debug("Saved StockMovement object: ", );
            
        log.info("Inventory return completed for vendorId: {}", request.getVendorId());                
    }

    public void recordAdjustStock(AdjustStockRequest request, UUID performedBy, Integer beforeQuantity){
        log.debug("Building StockMovement: Type=ADJUST, Delta={}, Before={}, After={}, Reason={}", 
            request.getDelta(), beforeQuantity, (beforeQuantity + request.getDelta()), request.getNotes());
        
        StockMovementType type = StockMovementType.ADJUSTMENT;

        movementRepo.save(
            Objects.requireNonNull(
                buildStockMovement(type, beforeQuantity, request.getNotes(), performedBy, request)
            )
        );

        log.info("StockMovement saved successfully. Vendor: {}, Delta: {}", request.getVendorId(), request.getDelta());
    }

    public void recordOutBoundReserveStock(CreateOutBoundRequest request , Integer beforeQuantity){
        log.debug("Building StockMovement: productVariantId={}, warehouseId={}, Type=OUTBOUND, Before={}, After={}", 
            request.getProductVariantId(), request.getWarehouseId(), beforeQuantity, (beforeQuantity - request.getQuantity()));

        StockMovementType type = StockMovementType.OUTBOUND;

        movementRepo.save(
            Objects.requireNonNull(
                buildStockMovement(type, beforeQuantity,null, request.getVendorId(), request)
            )
        );
        log.info("StockMovement saved successfully. Vendor: {}, productVariantId={}, warehouseId={}", request.getVendorId(), request.getProductVariantId(), request.getWarehouseId());
    }

    public void recordOutBoundReleaseStock(CreateOutBoundRequest request , Integer beforeQuantity){
        log.debug("Building StockMovement: productVariantId={}, warehouseId={}, Type=OUTBOUND, Before={}, After={}", 
            request.getProductVariantId(), request.getWarehouseId(), beforeQuantity, (beforeQuantity - request.getQuantity()));

        StockMovementType type = StockMovementType.OUTBOUND;

        movementRepo.save(
            Objects.requireNonNull(
                buildStockMovement(type, beforeQuantity,null, request.getVendorId(), request)
            )
        );
        
        log.info("StockMovement saved successfully. Vendor: {}, productVariantId={}, warehouseId={}", request.getVendorId(), request.getProductVariantId(), request.getWarehouseId());
    }

    private StockMovement buildStockMovement(StockMovementType type, Integer beforeQuantity, String notes, UUID performedBy,StockMovementRequest request){
        return StockMovement.builder()
                    .type(type)
                    .vendorId(request.getVendorId())
                    .createdBy(performedBy.toString())
                    .productVariantId(request.getProductVariantId())
                    .beforeQuantity(beforeQuantity)
                    .afterQuantity(beforeQuantity - request.getQuantity())
                    .referenceType(ReferenceType.ORDER_RELEASE.toString())
                    .warehouseId(request.getWarehouseId())
                    .notes(notes)
                    .build();
    }

}

