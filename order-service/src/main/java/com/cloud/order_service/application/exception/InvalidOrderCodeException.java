package com.cloud.order_service.application.exception;

import com.cloud.order_service.common.exception.BadRequestException;

public class InvalidOrderCodeException extends BadRequestException{
    public InvalidOrderCodeException(String message){
        super(message);
    }
}
