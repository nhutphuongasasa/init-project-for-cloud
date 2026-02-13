package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ForbiddenException;

public class VendorNotOwnedException extends ForbiddenException{
    public VendorNotOwnedException(String message){
        super(message);
    }
}
