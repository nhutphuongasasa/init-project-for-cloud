package com.cloud.auth_service.infrastructure.adapter.outbound.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.auth_service.domain.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID>{
    
}
