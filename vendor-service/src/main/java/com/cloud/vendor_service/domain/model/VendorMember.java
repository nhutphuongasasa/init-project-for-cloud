package com.cloud.vendor_service.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.cloud.vendor_service.domain.enums.VendorMemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author nhutphuong
 * @version 2
 * @created 14/01/2026 14:46h
 */
@Entity
@Table(name = "vendor_members")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorMember {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Default
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VendorMemberStatus status = VendorMemberStatus.ACTIVE;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}