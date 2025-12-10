package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.ResourceNotFoundException;

public class StockMovementNotFoundException extends ResourceNotFoundException {
    public StockMovementNotFoundException() {
        super("StockMovement");
    }
}
