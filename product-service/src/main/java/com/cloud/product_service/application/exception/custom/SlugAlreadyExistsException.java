package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ResourceAlreadyExistsException;

public class SlugAlreadyExistsException extends ResourceAlreadyExistsException {
    public SlugAlreadyExistsException(String slug) {
        super("slug", slug);
    }
}
