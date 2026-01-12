package com.cloud.vendor_service.infastructure.exception.custom;

import com.cloud.vendor_service.common.exception.ResourceAlreadyExistsException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
public class SlugAlreadyExistsException extends ResourceAlreadyExistsException {
    public SlugAlreadyExistsException(String slug) {
        super("slug", slug);
    }
}
