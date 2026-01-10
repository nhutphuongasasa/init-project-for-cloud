package com.cloud.auth_service.infrastructure.adapter.outbound.repository;

import com.cloud.auth_service.domain.model.User; // Giả sử model của bạn ở đây
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);
}