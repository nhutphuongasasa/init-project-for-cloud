package com.cloud.product_service.application.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class updateBasicInfoProduct {
    private String name;
    private String description;
    private String slug;
    private UUID categoryId;
}
