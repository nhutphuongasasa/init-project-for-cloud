package com.cloud.inventory.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(
            GlobalErrorCode.CONFLICT,
            message,
            HttpStatus.CONFLICT
        );
    }
}
