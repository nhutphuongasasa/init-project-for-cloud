package com.cloud.vendor_service.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.cloud.vendor_service.domain.enums.VendorMemberStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "vendor_members",
    indexes = {
        @Index(name = "idx_vendor_members_vendor", columnList = "vendor_id"),
        @Index(name = "idx_vendor_members_user", columnList = "user_id"),
        @Index(name = "idx_vendor_members_status", columnList = "status")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class VendorMember {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Default
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VendorMemberStatus status = VendorMemberStatus.ACTIVE;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}