package com.cloud.auth_service.common.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseException {
    public InternalServerErrorException(String message) {
        super(
            GlobalErrorCode.INTERNAL_SERVER_ERROR,
            message,
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
