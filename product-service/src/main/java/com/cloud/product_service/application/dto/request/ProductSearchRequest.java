package com.cloud.product_service.application.dto.request;

import java.util.UUID;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchRequest {
    private String name;
    private String slug;
    private UUID categoryId;
    private UUID vendorId;//filed cho admin
    private String status;
    private Integer price;
    private Integer originalPrice;
    private String sortBy = "createdAt";
    private Sort.Direction sortDirection = Sort.Direction.ASC;
}
