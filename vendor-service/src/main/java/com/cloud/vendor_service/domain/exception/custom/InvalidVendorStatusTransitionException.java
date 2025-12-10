package com.cloud.vendor_service.domain.exception.custom;

import com.cloud.vendor_service.common.exception.InvalidStatusException;

public class InvalidVendorStatusTransitionException extends InvalidStatusException{
    public InvalidVendorStatusTransitionException(String message){
        super(message);
    }
}
