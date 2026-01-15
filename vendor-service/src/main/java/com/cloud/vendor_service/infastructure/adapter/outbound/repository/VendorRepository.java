package com.cloud.vendor_service.infastructure.adapter.outbound.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cloud.vendor_service.domain.enums.VendorStatus;
import com.cloud.vendor_service.domain.model.Vendor;
import org.springframework.lang.NonNull;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, UUID>, JpaSpecificationExecutor<Vendor> {

    Optional<Vendor> findByIdAndStatus(UUID id, VendorStatus status);

    Optional<List<Vendor>> findByStatus(VendorStatus status);

    boolean existsById(@NonNull UUID id);
    
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID excludedId);

    Optional<Vendor> findBySlug(String slug);

    Page<Vendor> findByStatus(VendorStatus status, Pageable pageable);
    Page<Vendor> findByStatusIn(List<VendorStatus> statuses, Pageable pageable);

    @EntityGraph(attributePaths = {"profile", "settings"})
    Optional<Vendor> findWithProfileAndSettingsById(UUID id);

    @EntityGraph(attributePaths = {"profile", "settings"})
    Optional<Vendor> findWithProfileAndSettingsBySlug(String slug);
}