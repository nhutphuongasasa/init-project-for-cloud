package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException() {
        super("Category");
    }
}
