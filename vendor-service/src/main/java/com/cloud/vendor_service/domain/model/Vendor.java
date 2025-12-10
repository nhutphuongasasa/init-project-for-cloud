package com.cloud.vendor_service.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;


/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
@Entity
@Table(name = "vendors")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    // @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(updatable = false, nullable = false)
    private UUID id;


    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "description", length = 500)
    private String description;

    // @Column(name = "owner_user_id", nullable = false, length = 100)
    // private UUID ownerUserId; // Keycloak sub/user_id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VendorStatus status = VendorStatus.PENDING;

    // @Column(precision = 3, scale = 2)
    // @ColumnDefault("0.00")
    // @Builder.Default
    // private BigDecimal rating = BigDecimal.ZERO;

    // @Column(name = "total_sales")
    // @ColumnDefault("0")
    // @Builder.Default
    // private Long totalSales = 0L;

    @CreatedDate
    @Column(name = "joined_at", updatable = false)
    private Instant joinedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(mappedBy = "vendor", cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE,
        CascadeType.REFRESH,
        CascadeType.DETACH
    }, fetch = FetchType.LAZY, optional = false)
    private VendorProfile profile;

    // @OneToOne(mappedBy = "vendor", cascade = {
    //     CascadeType.PERSIST,
    //     CascadeType.MERGE,
    //     CascadeType.REFRESH,
    //     CascadeType.DETACH
    // }, fetch = FetchType.LAZY, optional = false)
    // private VendorSettings settings;
}
