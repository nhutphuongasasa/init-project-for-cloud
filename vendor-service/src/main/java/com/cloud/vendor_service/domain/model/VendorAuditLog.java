package com.cloud.vendor_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "vendor_audit_logs",
    indexes = {
        @Index(name = "idx_vendor_audit_logs_vendor", columnList = "vendor_id"),
        @Index(name = "idx_vendor_audit_logs_performed_by", columnList = "performed_by"),    }
)
@EntityListeners(AuditingEntityListener.class)
public class VendorAuditLog {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "action", nullable = false, length = 50)
    private String action;     

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_value")
    private JsonNode oldValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_value")
    private JsonNode newValue;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;         

    @Column(name = "performed_by", nullable = false)
    private UUID performedBy;       

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}