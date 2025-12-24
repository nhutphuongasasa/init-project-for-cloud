package com.cloud.product_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ProductImageRequest {
    @NotBlank(message = "URL is required")
    private String url;
    @NotNull(message = "Is main is required")
    private Boolean isMain;
}
