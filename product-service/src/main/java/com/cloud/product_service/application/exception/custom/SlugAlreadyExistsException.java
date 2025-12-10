package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ResourceAlreadyExistsException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 26/11/2025
 */
public class SlugAlreadyExistsException extends ResourceAlreadyExistsException {
    public SlugAlreadyExistsException(String slug) {
        super("slug", slug);
    }
}
