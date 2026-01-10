package com.cloud.auth_service.infrastructure.exception;

import com.cloud.auth_service.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException() {
        super("User");
    }
}
