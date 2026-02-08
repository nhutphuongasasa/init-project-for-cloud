package com.cloud.auth_service.infrastructure.adapter.outbound.repository;

import com.cloud.auth_service.domain.model.User; // Giả sử model của bạn ở đây
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    Boolean existsByEmail(String email);

    Boolean existsByRoles_Id(UUID roleId);
}