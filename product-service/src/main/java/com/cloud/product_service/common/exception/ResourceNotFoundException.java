package com.cloud.product_service.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String entity){
        super(
            GlobalErrorCode.NOT_FOUND,
            String.format("%s not found", entity),
            HttpStatus.NOT_FOUND
        );
    }
}