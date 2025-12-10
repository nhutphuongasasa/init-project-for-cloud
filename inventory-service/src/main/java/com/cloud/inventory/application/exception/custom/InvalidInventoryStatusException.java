package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.InvalidStatusException;

public class InvalidInventoryStatusException extends InvalidStatusException {
    public InvalidInventoryStatusException(String message) {
        super(message);
    }
}
