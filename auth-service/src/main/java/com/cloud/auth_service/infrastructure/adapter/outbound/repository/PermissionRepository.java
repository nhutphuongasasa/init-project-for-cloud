package com.cloud.auth_service.infrastructure.adapter.outbound.repository;

import com.cloud.auth_service.domain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Set;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    
    @Query("SELECT p.code FROM Permission p")
    Set<String> findAllCodes();

    long countByIdIn(Set<UUID> ids);
}