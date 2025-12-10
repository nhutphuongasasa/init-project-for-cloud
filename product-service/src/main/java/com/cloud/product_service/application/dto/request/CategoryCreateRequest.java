package com.cloud.product_service.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CategoryCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Slug is required")
    private String slug;
    @NotBlank(message = "Icon URL is required")
    private String iconUrl;
    private UUID parentId;
}
