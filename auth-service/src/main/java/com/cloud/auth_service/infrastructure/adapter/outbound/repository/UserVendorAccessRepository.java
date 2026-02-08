package com.cloud.auth_service.infrastructure.adapter.outbound.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cloud.auth_service.domain.model.UserVendorAccess;
import com.cloud.auth_service.domain.model.UserVendorAccessId;

@Repository
public interface UserVendorAccessRepository extends JpaRepository<UserVendorAccess, UserVendorAccessId> {

    // Lấy thông tin truy cập duy nhất của User
    @Query("SELECT uva FROM UserVendorAccess uva " +
           "JOIN FETCH uva.role r " + 
           "WHERE uva.userId = :userId")
    Optional<UserVendorAccess> findByUserId(@Param("userId") UUID userId);
}