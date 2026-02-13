package com.cloud.product_service.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.cloud.product_service.application.dto.response.ProductReportResponse;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.application.dto.response.StockSummaryDto;
import com.cloud.product_service.domain.enums.ProductStatus;
import com.cloud.product_service.domain.model.Product;
import com.cloud.product_service.domain.model.ProductVariant;
import com.cloud.product_service.infrastructure.adapter.outbound.openfeign.client.InventoryClient;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductRepository;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.Specification.ProductSpecification;
import com.cloud.product_service.common.utils.jwt.JwtUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueryService {
    private final ProductRepository productRepository;
    private final JwtUtils jwtUtils;
    private final ProductMapper productMapper;
    private final InventoryClient inventoryClient;
    
    public Product getProductEntityById(UUID id){
        return productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found with id={}", id);
                return new ProductNotFoundException();
            });
    }

    public List<ProductResponse> searchVariantsByKeyword(String keyword) {
        UUID currentUserId = jwtUtils.getCurrentUserId();

        List<ProductResponse> result = productRepository.searchProducts(keyword, currentUserId)
                .stream()
                .map(productMapper::toProductResponse)
                .toList();

        if (result.isEmpty()) {
            log.debug("No product variants found for keyword='{}' by user {}", keyword, currentUserId);
        }

        return result;
    }

    public Page<ProductResponse> getAllActiveProduct(int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductResponse> result = productRepository.findByStatus(ProductStatus.ACTIVE, pageRequest)
            .map(productMapper::toProductResponse);

        return result;
    }
    
    public Page<ProductResponse> getAllProductsByVendorId(int page, int size, UUID vendorId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductResponse> result = productRepository.findByVendorIdAndStatus(vendorId, ProductStatus.ACTIVE, pageRequest)
            .map(productMapper::toProductResponse);

        return result;
    }

    public Page<ProductResponse> getAllMyProduct(int page, int size) {
        UUID vendorId = jwtUtils.getCurrentUserId();

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductResponse> result = productRepository.findByVendorId(vendorId, pageRequest)
            .map(productMapper::toProductResponse);

        return result;
    }

    public ProductReportResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> {
                log.warn("Product not found with id={}", productId);
                return new ProductNotFoundException();
            });
        
        if(!product.getVendorId().equals(jwtUtils.getCurrentUserId())){
            log.warn("Product not found with id={}", productId);
            throw new ProductNotFoundException();
        }

        List<UUID> varianList = product.getVariants().stream().map(ProductVariant::getId).collect(Collectors.toList());

        Map<UUID, List<StockSummaryDto>> stockMap = inventoryClient.getStockByVariantIds(varianList);

        ProductReportResponse response = productMapper.toProductReportResponse(product, stockMap);
        return response;
    }

    public Page<ProductResponse> getAllProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductResponse> result = productRepository.findAll(pageRequest)
            .map(productMapper::toProductResponse);

        return result;
    }

    public Page<ProductResponse> searchMyProduct(int page, int size, ProductSearchRequest search) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        Page<ProductResponse> result = specSearch(page, size, search, vendorId);

        return result;
    }

    public Page<ProductResponse> searchProduct(int page, int size, ProductSearchRequest search) {
        Page<ProductResponse> result = specSearch(page, size, search, null);
        
        return result;
    }

    private Page<ProductResponse> specSearch(int page, int size, ProductSearchRequest search, UUID vendorId){
        PageRequest pageRequest = createPageRequest(page, size, search);

        Specification<Product> spec = ProductSpecification.fromRequest(search, vendorId == null ? null : vendorId);

        Page<ProductResponse> result = productRepository.findAll(spec, pageRequest)
            .map(productMapper::toProductResponse);
        return result;
    }

    private PageRequest createPageRequest(int page, int size, ProductSearchRequest search) {
        Sort sort = Sort.by(
            search.getSortDirection(),
            search.getSortBy() != null ? search.getSortBy() : "createdAt"
        );
        return PageRequest.of(page, size, sort);
    }
}
