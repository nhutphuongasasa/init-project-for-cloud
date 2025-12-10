// package com.cloud.vendor_service.domain.model;

// import jakarta.persistence.*;
// import lombok.*;
// import org.hibernate.annotations.JdbcTypeCode;
// import org.hibernate.type.SqlTypes;
// import java.math.BigDecimal;
// import java.util.UUID;

// @Entity
// @Table(name = "vendor_settings")
// @Getter 
// @Setter 
// @NoArgsConstructor 
// @AllArgsConstructor 
// @Builder
// public class VendorSettings {

//     @Id
//     @Column(name = "vendor_id")
//     private UUID vendorId;

//     @OneToOne(fetch = FetchType.LAZY)
//     @MapsId
//     @JoinColumn(name = "vendor_id")
//     private Vendor vendor;

//     @Column(name = "allow_cod")
//     @Builder.Default
//     private Boolean allowCod = true;

//     @Column(name = "free_shipping_min", precision = 12, scale = 2)
//     @Builder.Default
//     private BigDecimal freeShippingMin = BigDecimal.ZERO;

//     @Column(name = "shipping_policy", columnDefinition = "jsonb")
//     @JdbcTypeCode(SqlTypes.JSON)
//     @Builder.Default
//     private String shippingPolicy = "{}";

//     @Column(name = "return_policy", columnDefinition = "jsonb")
//     @JdbcTypeCode(SqlTypes.JSON)
//     @Builder.Default
//     private String returnPolicy = "{}";

//     @Column(name = "auto_accept_order")
//     @Builder.Default
//     private Boolean autoAcceptOrder = false;

//     @Column(name = "notification_pref", columnDefinition = "jsonb")
//     @JdbcTypeCode(SqlTypes.JSON)
//     @Builder.Default
//     private String notificationPref = "{\"new_order\":true,\"low_stock\":true,\"review\":true}";

//     @Column(name = "default_warehouse_id")
//     private Long defaultWarehouseId;
// }