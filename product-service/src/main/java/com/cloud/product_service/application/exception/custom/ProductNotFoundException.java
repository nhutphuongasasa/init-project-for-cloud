package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ResourceNotFoundException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 26/11/2025
 */
public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException() {
        super("Product");
    }
}

