package com.cloud.vendor_service.infrastructure.adapter.outbound.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.vendor_service.domain.enums.VendorMemberStatus;
import com.cloud.vendor_service.domain.model.VendorMember;

public interface VendorMemberRepository extends JpaRepository<VendorMember, UUID> {
    boolean existsByVendorIdAndUserIdAndStatus(UUID vendorId, UUID userId, VendorMemberStatus status);
    Optional<VendorMember> findByVendorIdAndUserIdAndStatus(UUID vendorId, UUID userId, VendorMemberStatus status);
    List<VendorMember> findByUserIdAndStatus(UUID userId, VendorMemberStatus status);
    List<VendorMember> findByVendorIdAndStatus(UUID vendorId, VendorMemberStatus status);
    boolean existsByUserIdAndStatus(UUID userId, VendorMemberStatus status);    
}
