package com.cloud.product_service.application.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {
    @NotNull(message = "Price is required")
    private BigDecimal price;
    @NotNull(message = "Original price is required")
    private BigDecimal originalPrice;
    @NotNull(message = "Attributes is required")
    private JsonNode attributes;
    @NotNull(message = "Weight gram is required")
    private Integer weightGram;
    private List<ProductImageRequest> images;
}
