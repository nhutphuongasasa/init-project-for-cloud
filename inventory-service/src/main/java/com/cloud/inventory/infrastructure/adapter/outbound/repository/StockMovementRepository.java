package com.cloud.inventory.infrastructure.adapter.outbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cloud.inventory.domain.model.StockMovement;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    Page<StockMovement> findByProductVariantIdAndVendorIdOrderByCreatedAtDesc(UUID variantId, UUID vendorId, Pageable pageable);
        
    @Query("SELECT sm FROM StockMovement sm " +
        "WHERE sm.productVariantId = :variantId " +
        "AND sm.vendorId = :vendorId " +
        "ORDER BY sm.createdAt DESC")
    Page<StockMovement> findByProductVariantIdAndVendorId(
        @Param("variantId") UUID variantId,
        @Param("vendorId") UUID vendorId,
        Pageable pageable);
}
