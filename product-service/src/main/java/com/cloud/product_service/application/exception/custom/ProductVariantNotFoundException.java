package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ResourceNotFoundException;

public class ProductVariantNotFoundException extends ResourceNotFoundException{
    public ProductVariantNotFoundException() {
        super("Product variant ");
    }
    
}
