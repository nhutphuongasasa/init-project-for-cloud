package com.cloud.vendor_service.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidOperationException extends BaseException {
    public InvalidOperationException(String message){
        super(
            GlobalErrorCode.INVALID_STATUS,
            message,
            HttpStatus.BAD_REQUEST
        );
    }
}
