package com.cloud.product_service.application.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantResponse {
    private UUID id;
    private String sku;
    private UUID vendorId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Object attributes;
    private Integer weightGram;
    private List<ProductImageResponse> images;
}