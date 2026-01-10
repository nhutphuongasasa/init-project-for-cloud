package com.cloud.auth_service.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusException extends BaseException{
    public InvalidStatusException(String message){
        super(
            GlobalErrorCode.INVALID_STATUS,
            message,
            HttpStatus.BAD_REQUEST
        );
    }
}
