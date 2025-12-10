package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ForbiddenException;

public class CategoryAlreadyExistsException extends ForbiddenException{
    public CategoryAlreadyExistsException(String message){
        super(message);
    }
}
