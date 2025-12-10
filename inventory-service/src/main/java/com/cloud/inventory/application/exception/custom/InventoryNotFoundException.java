package com.cloud.inventory.application.exception.custom;

import com.cloud.inventory.common.exception.ResourceNotFoundException;

public class InventoryNotFoundException extends ResourceNotFoundException {
    public InventoryNotFoundException() {
        super("Inventory");
    }
}
