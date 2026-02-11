package com.cloud.vendor_service.common.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class UnAuthorizedException extends BaseException {
    
    public UnAuthorizedException(String message, Object ... args) {
        super(
            GlobalErrorCode.UNAUTHORIZED,
            message, 
            HttpStatus.UNAUTHORIZED, 
            Map.of("args", args)
        );
    }
}