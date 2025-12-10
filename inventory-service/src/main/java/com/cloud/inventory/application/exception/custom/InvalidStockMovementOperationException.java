package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.InvalidOperationException;

public class InvalidStockMovementOperationException extends InvalidOperationException {
    public InvalidStockMovementOperationException(String message) {
        super(message);
    }
}
