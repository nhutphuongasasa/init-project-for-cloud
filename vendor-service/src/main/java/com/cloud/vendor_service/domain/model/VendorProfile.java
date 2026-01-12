package com.cloud.vendor_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
@Entity
@Table(name = "vendor_profiles")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@EntityListeners(AuditingEntityListener.class)
public class VendorProfile {

    @Id
    @Column(name = "vendor_id")
    private UUID vendorId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "social_links", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode socialLinks;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}