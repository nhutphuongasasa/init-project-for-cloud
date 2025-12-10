package com.cloud.product_service.application.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cloud.product_service.application.exception.custom.ProductNotFoundException;
import com.cloud.product_service.application.mapper.ProductMapper;
import com.cloud.product_service.application.dto.request.ProductSearchRequest;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.domain.enums.ProductStatus;
import com.cloud.product_service.domain.model.Product;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductRepository;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.Specification.ProductSpecification;
import com.cloud.product_service.common.utils.jwt.JwtUtils;

/**
 * @author: nhutphuong
 * @date: 2025-11-28
 * @version: 1.1
 * @description: service query product with logging
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueryService {
    private final ProductRepository productRepository;
    private final JwtUtils jwtUtils;
    private final ProductMapper productMapper;
    
    public Product getProductEntityById(UUID id){
        log.debug("Fetching product entity by id={}", id);
        return productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found with id={}", id);
                return new ProductNotFoundException();
            });
    }

    public Page<ProductResponse> getAllActiveProduct(int page, int size){
        log.debug("Fetching all active products page={} size={}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<ProductResponse> result = productRepository.findByStatus(ProductStatus.ACTIVE, pageRequest)
            .map(productMapper::toProductResponse);
        log.info("Found {} active products in total", result.getTotalElements());
        return result;
    }
    
    public Page<ProductResponse> getAllProductsByVendorId(int page, int size) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        log.debug("Fetching all products for vendorId={} page={} size={}", vendorId, page, size);
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<ProductResponse> result = productRepository.findByVendorIdAndStatus(vendorId, ProductStatus.ACTIVE, pageRequest)
            .map(productMapper::toProductResponse);
        log.info("Found {} active products for vendorId={}", result.getTotalElements(), vendorId);
        return result;
    }

    public Page<ProductResponse> getMyAllProduct(int page, int size) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        log.debug("Fetching all products (any status) for vendorId={} page={} size={}", vendorId, page, size);
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<ProductResponse> result = productRepository.findByVendorId(vendorId, pageRequest)
            .map(productMapper::toProductResponse);
        log.info("Found {} products for vendorId={}", result.getTotalElements(), vendorId);
        return result;
    }

    public ProductResponse getProductById(UUID productId) {
        log.debug("Fetching product by id={}", productId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> {
                log.warn("Product not found with id={}", productId);
                return new ProductNotFoundException();
            });
        log.info("Product fetched successfully id={}", productId);
        return productMapper.toProductResponse(product);
    }

    public Page<ProductResponse> getAllProducts(int page, int size) {
        log.debug("Fetching all products page={} size={}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<ProductResponse> result = productRepository.findAll(pageRequest)
            .map(productMapper::toProductResponse);
        log.info("Found {} products in total", result.getTotalElements());
        return result;
    }

    public Page<ProductResponse> searchMyProduct(int page, int size, ProductSearchRequest search) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        log.debug("Searching products for vendorId={} with criteria={} page={} size={}", vendorId, search, page, size);
        PageRequest pageRequest = PageRequest.of(
            page, 
            size,
            Sort.by(search.getSortDirection(), search.getSortBy() != null ? search.getSortBy() : "createdAt")
        );

        Specification<Product> spec = ProductSpecification.fromRequest(search, vendorId);

        Page<ProductResponse> result = productRepository.findAll(spec, pageRequest)
            .map(productMapper::toProductResponse);
        log.info("Search found {} products for vendorId={}", result.getTotalElements(), vendorId);
        return result;
    }

    public Page<ProductResponse> searchProduct(int page, int size, ProductSearchRequest search) {
        log.debug("Searching all products with criteria={} page={} size={}", search, page, size);
        PageRequest pageRequest = PageRequest.of(
            page, 
            size,
            Sort.by(search.getSortDirection(), search.getSortBy() != null ? search.getSortBy() : "createdAt")
        );

        Specification<Product> spec = ProductSpecification.fromRequest(search, null);

        Page<ProductResponse> result = productRepository.findAll(spec, pageRequest)
            .map(productMapper::toProductResponse);
        log.info("Search found {} products in total", result.getTotalElements());
        return result;
    }
}
