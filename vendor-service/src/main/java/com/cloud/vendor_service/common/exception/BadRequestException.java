package com.cloud.vendor_service.common.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class BadRequestException extends BaseException {
    public BadRequestException(String message, Object ... args) {
        super(
            GlobalErrorCode.BAD_REQUEST, 
            message, 
            HttpStatus.BAD_REQUEST, 
            Map.of("args", args)
        );
    }
}

