// package com.cloud.vendor_service.domain.model;

// import jakarta.persistence.*;
// import lombok.*;
// import org.hibernate.annotations.JdbcTypeCode;
// import org.hibernate.type.SqlTypes;
// import org.springframework.data.annotation.CreatedDate;

// import java.time.Instant;

// @Entity
// @Table(name = "vendor_audit_logs", indexes = {
//     @Index(name = "idx_action", columnList = "action, created_at")
// })
// @Getter 
// @Setter 
// @NoArgsConstructor 
// @AllArgsConstructor 
// @Builder
// public class VendorAuditLog {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "vendor_id", nullable = false)
//     private Long vendorId;

//     @Column(nullable = false, length = 100)
//     private String action; // REGISTER, APPROVE, SUSPEND, UPDATE_PROFILE...

//     @Column(name = "performed_by", length = 100)
//     private String performedBy; // user_id hoặc ADMIN

//     @Column(columnDefinition = "jsonb")
//     @JdbcTypeCode(SqlTypes.JSON)
//     private String oldValues;

//     @Column(columnDefinition = "jsonb")
//     @JdbcTypeCode(SqlTypes.JSON)
//     private String newValues;

//     @Column(name = "ip_address", length = 45)
//     private String ipAddress;

//     @Column(name = "user_agent", length = 500)
//     private String userAgent;

//     @CreatedDate
//     @Column(name = "created_at")
//     private Instant createdAt;
// }