package com.cloud.product_service.application.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchProductResult {
    private String productName;
    private String productCode;
    private String sku;
    private JsonNode attributes;
}
