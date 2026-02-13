package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.InvalidOperationException;

public class VendorStatusException extends InvalidOperationException {
    public VendorStatusException(String message){
        super(message);
    }
}
