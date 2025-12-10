package com.cloud.order_service.common.exception;

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
