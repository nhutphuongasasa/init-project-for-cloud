package com.cloud.inventory.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(
            GlobalErrorCode.UNAUTHORIZED,
            message,
            HttpStatus.UNAUTHORIZED
        );
    }
}
