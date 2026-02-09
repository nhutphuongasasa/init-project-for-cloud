package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.BadRequestException;

public class VendMemberAlreadyExistsException extends BadRequestException{
    public VendMemberAlreadyExistsException(String message, Object ... args) {
        super(
            message,
            args
        );
    }    
}
