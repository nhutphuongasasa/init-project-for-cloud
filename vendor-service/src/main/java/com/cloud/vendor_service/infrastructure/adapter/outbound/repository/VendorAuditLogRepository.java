package com.cloud.vendor_service.infrastructure.adapter.outbound.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.vendor_service.domain.model.VendorAuditLog;

public interface VendorAuditLogRepository extends JpaRepository<VendorAuditLog, UUID> {
    
}
