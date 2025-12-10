package com.cloud.inventory.common.exception;

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