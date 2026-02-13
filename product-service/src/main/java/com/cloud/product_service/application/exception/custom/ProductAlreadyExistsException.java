package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ForbiddenException;

public class ProductAlreadyExistsException extends ForbiddenException{
    public ProductAlreadyExistsException(String message){
        super(message);
    }
}
