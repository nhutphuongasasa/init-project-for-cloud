package com.cloud.vendor_service.application.exception.custom;

import java.util.UUID;

import com.cloud.vendor_service.common.exception.ResourceNotFoundException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
public class VendorNotFoundException extends ResourceNotFoundException {
    public VendorNotFoundException(UUID id) {
        super("id", id.toString());
    }
    public VendorNotFoundException(String slug) {
        super("slug", slug);
    }
}

