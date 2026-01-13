package com.cloud.auth_service.infrastructure.exception;

import com.cloud.auth_service.common.exception.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException{
    public RoleNotFoundException() {
        super("User");
    }
}
