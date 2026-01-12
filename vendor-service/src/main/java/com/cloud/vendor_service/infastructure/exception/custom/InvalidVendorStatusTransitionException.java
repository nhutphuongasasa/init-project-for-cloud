package com.cloud.vendor_service.infastructure.exception.custom;

import com.cloud.vendor_service.common.exception.InvalidStatusException;

public class InvalidVendorStatusTransitionException extends InvalidStatusException{
    public InvalidVendorStatusTransitionException(String message){
        super(message);
    }
}
