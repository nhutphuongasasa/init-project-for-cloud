package com.cloud.vendor_service.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String field, String value){
        super(
            GlobalErrorCode.NOT_FOUND,
            String.format("%s not found with value: %s", field, value),
            HttpStatus.NOT_FOUND
        );
    }
}
