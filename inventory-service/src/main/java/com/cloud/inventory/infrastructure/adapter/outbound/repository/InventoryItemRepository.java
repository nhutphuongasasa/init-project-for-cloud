package com.cloud.inventory.infrastructure.adapter.outbound.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cloud.inventory.application.dto.WarehouseDto;
import com.cloud.inventory.domain.model.InventoryItem;
import com.cloud.inventory.domain.model.InventoryItemId;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, InventoryItemId> {

    Optional<InventoryItem> findByProductVariantIdAndWarehouseId(UUID productVariantId, UUID warehouseId);

    @Query("SELECT i FROM InventoryItem i WHERE i.productVariantId IN :variantIds " +
           "AND i.vendorId = :vendorId")
    List<InventoryItem> findByProductVariantIdInAndVendorId(
            @Param("variantIds") List<UUID> variantIds,
            @Param("vendorId") UUID vendorId);

    List<InventoryItem> findByProductVariantIdAndVendorId(UUID variantId, UUID vendorId);

    List<InventoryItem> findByVendorId(UUID vendorId);

@Query("""
    SELECT i FROM InventoryItem i
    JOIN i.warehouse w
    WHERE i.vendorId = :vendorId
      AND (:keyword IS NULL OR :keyword = '') 
      AND (COALESCE(:warehouseIds) IS NULL OR w.id IN :warehouseIds)
      AND (:minStock IS NULL OR i.quantityAvailable >= :minStock)
      AND (:maxStock IS NULL OR i.quantityAvailable <= :maxStock)
    ORDER BY i.lastUpdated DESC
    """)
Page<InventoryItem> searchStock(
    @Param("vendorId") UUID vendorId,
    @Param("keyword") String keyword,
    @Param("warehouseIds") List<UUID> warehouseIds,
    @Param("minStock") Integer minStock,
    @Param("maxStock") Integer maxStock,
    Pageable pageable);

@Query("""
    SELECT DISTINCT new com.cloud.inventory.application.dto.WarehouseDto(
        w.id, w.code, w.name, w.address
    )
    FROM InventoryItem i 
    JOIN i.warehouse w 
    WHERE i.vendorId = :vendorId
    """)
List<WarehouseDto> findDistinctWarehouseByVendorId(@Param("vendorId") UUID vendorId);
}
