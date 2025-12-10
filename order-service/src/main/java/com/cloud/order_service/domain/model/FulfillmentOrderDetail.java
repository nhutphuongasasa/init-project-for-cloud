package com.cloud.order_service.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fulfillment_order_details")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FulfillmentOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private FulfillmentOrder order;

    @Column(name = "product_variant_id", nullable = false)
    private UUID productVariantId;

    // @Column(name = "sku", nullable = false, length = 100)
    // private String sku;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "quantity_requested", nullable = false)
    private Integer quantityRequested;

    @Builder.Default
    @Column(name = "quantity_picked")
    private Integer quantityPicked = 0;

    @Column(name = "unit_price", precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @Column(columnDefinition = "TEXT")
    private String notes;
}