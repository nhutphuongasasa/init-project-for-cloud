package com.cloud.product_service.application.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductImageResponse {
    private UUID id;
    private UUID productId;
    private String url;
    private Integer sortOrder;
    private Boolean isMain;
}
