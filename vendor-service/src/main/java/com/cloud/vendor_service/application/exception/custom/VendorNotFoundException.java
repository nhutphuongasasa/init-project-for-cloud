package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.ResourceNotFoundException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
public class VendorNotFoundException extends ResourceNotFoundException {
    public VendorNotFoundException(Object ... args) {
        super("Vendor", args);
    }
}

