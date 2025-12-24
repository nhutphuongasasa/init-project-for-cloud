package com.cloud.product_service.infrastructure.adapter.outbound.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cloud.product_service.domain.enums.ProductStatus;
import com.cloud.product_service.domain.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @Query("""
    SELECT p
    FROM Product p
    WHERE p.vendorId = :vendorId
      AND p.status = 'ACTIVE'
      AND :keyword IS NOT NULL
      AND (
            EXISTS (SELECT 1 FROM ProductVariant pv WHERE pv.product = p AND LOWER(pv.sku) LIKE LOWER(CONCAT(:keyword, '%')))
         OR LOWER(p.productCode) LIKE LOWER(CONCAT(:keyword, '%'))
         OR LOWER(p.name) LIKE LOWER(CONCAT(:keyword, '%'))
      )
    ORDER BY 
      CASE 
        WHEN EXISTS (SELECT 1 FROM ProductVariant pv WHERE pv.product = p AND LOWER(pv.sku) = LOWER(:keyword)) THEN 1
        WHEN LOWER(p.productCode) = LOWER(:keyword) THEN 2
        ELSE 3 
      END ASC,
      LOWER(p.name) ASC
    """)
List<Product> searchProducts(
    @Param("keyword") String keyword,
    @Param("vendorId") UUID vendorId
);
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