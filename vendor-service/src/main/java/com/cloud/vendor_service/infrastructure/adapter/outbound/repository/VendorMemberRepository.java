package com.cloud.vendor_service.infrastructure.adapter.outbound.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.vendor_service.domain.model.VendorMember;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/1/15 20:27h
 */
public interface VendorMemberRepository extends JpaRepository<VendorMember, UUID> {
    
}
