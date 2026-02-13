package com.cloud.vendor_service.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.cloud.vendor_service.domain.enums.VendorStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "vendors",
    indexes = {
        @Index(name = "idx_vendors_slug", columnList = "slug"),
        @Index(name = "idx_vendors_status", columnList = "status"),
        @Index(name = "idx_vendors_owner", columnList = "owner_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
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

    public void addNewProfile(String email) {
        VendorProfile newProfile = VendorProfile.builder()
            .email(email)
            .vendor(this)
            .build();
        
        this.setProfile(newProfile);
    }

}
