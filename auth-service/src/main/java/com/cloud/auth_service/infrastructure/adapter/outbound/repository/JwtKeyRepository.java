package com.cloud.auth_service.infrastructure.adapter.outbound.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.auth_service.domain.model.JwtKeys;

/**
 * @nhutphuong
 * @since 2026/1/11 12h
 * @version 1
 */
@Repository
public interface JwtKeyRepository extends JpaRepository<JwtKeys, UUID>{

}
