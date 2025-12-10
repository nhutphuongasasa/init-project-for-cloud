package com.cloud.vendor_service.infastructure.adapter.outbound.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.vendor_service.domain.model.VendorProfile;

@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, UUID> {
    Optional<VendorProfile> findByVendorId(UUID vendorId);
    Optional<VendorProfile> findByEmail(String email);
    Optional<VendorProfile> findByTaxCode(String taxCode);
}
