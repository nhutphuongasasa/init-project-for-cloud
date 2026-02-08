package com.cloud.vendor_service.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(String field, Object... args) {
        super(
            GlobalErrorCode.ALREADY_EXISTS,
            String.format("%s already exists ", field), 
            HttpStatus.CONFLICT,
            Map.of("field", field, "args", args)
        );
    }
}
