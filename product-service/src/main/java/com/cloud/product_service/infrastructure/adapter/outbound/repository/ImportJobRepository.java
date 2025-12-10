package com.cloud.product_service.infrastructure.adapter.outbound.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.product_service.domain.model.ImportJob;

@Repository
public interface ImportJobRepository extends JpaRepository<ImportJob, UUID>{
    List<ImportJob> findTop20ByVendorIdOrderByCreatedAtDesc(UUID vendorId);
}