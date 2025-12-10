package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.InvalidStatusException;

public class InvalidProductStatusTransitionException extends InvalidStatusException{
    public InvalidProductStatusTransitionException(String message){
        super(message);
    }
}
