package com.cloud.product_service.infrastructure.adapter.outbound.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cloud.product_service.domain.enums.ProductStatus;
import com.cloud.product_service.domain.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID id);
    Page<Product> findByVendorIdAndStatus(UUID vendorId, ProductStatus status, Pageable pageable);
    Integer countByVendorId(UUID vendorId);
    Page<Product> findByVendorId(UUID vendorId, Pageable pageable);
    List<Product> findByVendorId(UUID vendorId);
    Optional<Product> findByProductCodeAndVendorId(String productCode, UUID vendorId);
    Optional<Product> findByIdAndVendorId(UUID productId, UUID vendorId);
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}