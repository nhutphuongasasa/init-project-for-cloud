package com.cloud.product_service.application.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class updateBasicInfoProduct {
    private String name;
    private String description;
    private String slug;
    private UUID categoryId;
}
