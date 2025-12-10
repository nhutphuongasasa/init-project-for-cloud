package com.cloud.product_service.application.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ProductVariantResponse {
    private UUID id;
    // private UUID productId;
    private String sku;
    private UUID vendorId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Object attributes;
    private Integer weightGram;
    private List<ProductImageResponse> images;
}