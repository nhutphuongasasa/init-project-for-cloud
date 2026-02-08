package com.cloud.vendor_service.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message, Object... args) {
        super(
            GlobalErrorCode.FORBIDDEN,
            message,
            HttpStatus.FORBIDDEN,
            Map.of("args", args)
        );
    }
}
