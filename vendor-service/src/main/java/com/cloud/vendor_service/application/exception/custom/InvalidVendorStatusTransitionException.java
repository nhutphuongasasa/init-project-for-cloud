package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.InvalidStatusException;

public class InvalidVendorStatusTransitionException extends InvalidStatusException{
    public InvalidVendorStatusTransitionException(String message, Object ... args) {
        super(message, args);
    }
}
