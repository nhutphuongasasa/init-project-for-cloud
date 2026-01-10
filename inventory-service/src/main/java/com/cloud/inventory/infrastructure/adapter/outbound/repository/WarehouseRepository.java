package com.cloud.inventory.infrastructure.adapter.outbound.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.inventory.domain.model.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID>{
    Optional<Warehouse> findById(UUID id);
}
