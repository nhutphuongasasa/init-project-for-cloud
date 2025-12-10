package com.cloud.product_service.infrastructure.adapter.outbound.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import com.cloud.product_service.domain.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>{
    Optional<Category> findBySlug(String slug);
    Optional<Category> findById(UUID id);
    boolean existsBySlug(String slug);
}
