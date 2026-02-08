package com.cloud.vendor_service.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String field, Object... args) {
        super(
            GlobalErrorCode.NOT_FOUND,
            String.format("%s not found", field),
            HttpStatus.NOT_FOUND,
            Map.of("field", field, "args", args)
        );
    }
}
