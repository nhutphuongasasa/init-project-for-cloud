package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.ResourceNotFoundException;

public class InvitationNotFoundException extends ResourceNotFoundException {
    public InvitationNotFoundException(Object... args) {
        super("vendorMember", args);
    }
}
