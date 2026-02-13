package com.cloud.vendor_service.application.exception.custom;

import com.cloud.vendor_service.common.exception.ResourceAlreadyExistsException;

public class SlugAlreadyExistsException extends ResourceAlreadyExistsException {
    public SlugAlreadyExistsException(String slug, Object... args) {
        super("slug", slug, args);
    }
}
