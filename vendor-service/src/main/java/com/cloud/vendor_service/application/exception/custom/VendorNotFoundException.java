package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.ResourceNotFoundException;

public class VendorNotFoundException extends ResourceNotFoundException {
    public VendorNotFoundException(Object ... args) {
        super("Vendor", args);
    }
}

