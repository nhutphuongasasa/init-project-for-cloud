package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.ResourceAlreadyExistsException;

public class InventoryItemAlreadyExistsException extends ResourceAlreadyExistsException {
    public InventoryItemAlreadyExistsException(String field, String value) {
        super(field, value);
    }
}
