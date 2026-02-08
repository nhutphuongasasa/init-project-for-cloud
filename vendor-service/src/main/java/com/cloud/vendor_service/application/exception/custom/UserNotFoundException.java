package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException(Object... args) {
        super("User", args);
    }
}
