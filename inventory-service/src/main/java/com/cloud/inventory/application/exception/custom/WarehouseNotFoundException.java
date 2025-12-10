package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.ResourceNotFoundException;

public class WarehouseNotFoundException extends ResourceNotFoundException {
    public WarehouseNotFoundException() {
        super("Warehouse");
    }
}
