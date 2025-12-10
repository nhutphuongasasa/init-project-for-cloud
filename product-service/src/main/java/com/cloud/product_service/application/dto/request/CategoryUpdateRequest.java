package com.cloud.product_service.application.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryUpdateRequest {
    private String name;
    private String slug;
    private String iconUrl;
    private Boolean isActive;
    private UUID parentId;
}
