package com.cloud.product_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cloud.product_service.application.dto.request.ProductSearchRequest;
import com.cloud.product_service.application.dto.response.ProductReportResponse;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.application.service.ProductQueryService;
import com.cloud.product_service.common.response.FormResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/products-query")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryService productQueryService;

    @GetMapping("/filter")
    public ResponseEntity<FormResponse<List<ProductResponse>>> filterProductVariant(@RequestParam String keyword) {
        List<ProductResponse> result = productQueryService.filterProductVariant(keyword);
        return ResponseEntity.ok(FormResponse.<List<ProductResponse>>builder()
                .data(result)
                .message("Product variants filtered successfully")
                .timestamp(Instant.now())
                .build());
    }

    @GetMapping("/my")
    public ResponseEntity<FormResponse<Page<ProductResponse>>> getMyProducts(
            @ParameterObject Pageable pageable) {

        Page<ProductResponse> result = productQueryService.getMyAllProduct(
                pageable.getPageNumber(),
                pageable.getPageSize());

        return ResponseEntity.ok(FormResponse.<Page<ProductResponse>>builder()
                .data(result)
                .message("Products retrieved successfully")
                .timestamp(Instant.now())
                .build());
    }

    @GetMapping("/my/active")
    public ResponseEntity<FormResponse<Page<ProductResponse>>> getMyActiveProducts(
            @ParameterObject Pageable pageable) {

        Page<ProductResponse> result = productQueryService.getAllActiveProduct(
                pageable.getPageNumber(),
                pageable.getPageSize());

        return ResponseEntity.ok(FormResponse.<Page<ProductResponse>>builder()
                .data(result)
                .message("Active products retrieved successfully")
                .timestamp(Instant.now())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormResponse<ProductReportResponse>> getProductById(@PathVariable UUID id) {
        ProductReportResponse response = productQueryService.getProductById(id);

        return ResponseEntity.ok(FormResponse.<ProductReportResponse>builder()
                .data(response)
                .message("Product retrieved successfully")
                .timestamp(Instant.now())
                .build());
    }

    @GetMapping("/my/search")
    public ResponseEntity<FormResponse<Page<ProductResponse>>> searchMyProducts(
            @Valid @ParameterObject ProductSearchRequest search,
            @ParameterObject Pageable pageable) {

        Page<ProductResponse> result = productQueryService.searchMyProduct(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                search);

        return ResponseEntity.ok(FormResponse.<Page<ProductResponse>>builder()
                .data(result)
                .message("Search completed successfully")
                .timestamp(Instant.now())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<FormResponse<Page<ProductResponse>>> searchAllProducts(
            @Valid @ParameterObject ProductSearchRequest search,
            @ParameterObject Pageable pageable) {

        Page<ProductResponse> result = productQueryService.searchProduct(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                search);

        return ResponseEntity.ok(FormResponse.<Page<ProductResponse>>builder()
                .data(result)
                .message("Global search completed successfully")
                .timestamp(Instant.now())
                .build());
    }

    @GetMapping
    public ResponseEntity<FormResponse<Page<ProductResponse>>> getAllProducts(
            @ParameterObject Pageable pageable) {

        Page<ProductResponse> result = productQueryService.getAllProducts(
                pageable.getPageNumber(),
                pageable.getPageSize());

        return ResponseEntity.ok(FormResponse.<Page<ProductResponse>>builder()
                .data(result)
                .message("All products retrieved successfully")
                .timestamp(Instant.now())
                .build());
    }
}