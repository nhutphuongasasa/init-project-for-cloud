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

import com.cloud.vendor_service.domain.enums.VendorStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


/**
 * @author nhutphuong
 * @version 2
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
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
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

    private String code;

    @Column(name = "owner_id", nullable = false, length = 100)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VendorStatus status = VendorStatus.PENDING;

    @CreatedDate
    @Column(name = "joined_at", updatable = false)
    private Instant joinedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(mappedBy = "vendor")
    @JsonIgnore
    private VendorProfile profile;

    @JsonIgnore
    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private Set<VendorMember> members;

}
