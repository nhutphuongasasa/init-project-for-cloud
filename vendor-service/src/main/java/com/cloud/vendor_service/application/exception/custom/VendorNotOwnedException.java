package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.ForbiddenException;

public class VendorNotOwnedException extends ForbiddenException{
    public VendorNotOwnedException(String message, Object ... args) {
        super(message, args);
    }
}
