package com.cloud.order_service.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(String field, String value){
        super(
            GlobalErrorCode.ALREADY_EXISTS,
            String.format("%s already exists with value: %s", field, value),
            HttpStatus.CONFLICT
        );
    }
}
