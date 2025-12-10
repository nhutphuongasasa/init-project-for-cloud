package com.cloud.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Entity
@Builder
@Table(name = "inventory_items")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InventoryItemId.class)
public class InventoryItem {

    @Id
    @Column(name = "product_variant_id", nullable = false)
    private UUID productVariantId;

    @Id
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "quantity_reserved")
    private Integer quantityReserved;

    @Column(name = "safety_stock")
    private Integer safetyStock;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private Instant lastUpdated;
}
