package com.cloud.product_service.application.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String iconUrl;
    private Boolean isActive;
    private UUID parentId;
}
