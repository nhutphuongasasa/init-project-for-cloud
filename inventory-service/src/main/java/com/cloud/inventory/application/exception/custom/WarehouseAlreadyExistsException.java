package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.ResourceAlreadyExistsException;

public class WarehouseAlreadyExistsException extends ResourceAlreadyExistsException {
    public WarehouseAlreadyExistsException(String field, String value) {
        super(field, value);
    }
}
