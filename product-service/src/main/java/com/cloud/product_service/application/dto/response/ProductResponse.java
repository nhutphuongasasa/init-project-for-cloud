package com.cloud.product_service.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.cloud.product_service.domain.enums.ProductStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResponse {
    private UUID id;
    private UUID vendorId;
    private String name;
    private String slug;
    private String productCode;
    private String description;
    private UUID categoryId;
    private List<ProductVariantResponse> variants;
    private ProductStatus status;
    private Instant createdAt;
}