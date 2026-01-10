package com.cloud.auth_service.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message){
        super(
            GlobalErrorCode.FORBIDDEN,
            message,
            HttpStatus.FORBIDDEN
        );
    }
}
