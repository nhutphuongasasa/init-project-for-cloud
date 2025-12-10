package com.cloud.inventory.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(
            GlobalErrorCode.BAD_REQUEST,
            message,
            HttpStatus.BAD_REQUEST
        );
    }
}
