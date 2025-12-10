package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.ConflictException;

public class InsufficientStockException extends ConflictException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
