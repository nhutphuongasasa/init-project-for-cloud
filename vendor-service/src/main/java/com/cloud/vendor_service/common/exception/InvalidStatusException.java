package com.cloud.vendor_service.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class InvalidStatusException extends BaseException{
    public InvalidStatusException(String message, Object... args) {
        super(
            GlobalErrorCode.INVALID_STATUS,
            message,
            HttpStatus.BAD_REQUEST,
            Map.of("args", args)
        );
    }
}
