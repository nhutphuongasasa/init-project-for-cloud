package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.ForbiddenException;

public class VendorAlreadyExistsException extends ForbiddenException{
    public VendorAlreadyExistsException(String message, Object ... args) {
        super(message, args);
    }
}
