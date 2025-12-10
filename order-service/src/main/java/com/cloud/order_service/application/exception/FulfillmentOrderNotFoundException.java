package com.cloud.order_service.application.exception;

import com.cloud.order_service.common.exception.ResourceNotFoundException;

public class FulfillmentOrderNotFoundException extends ResourceNotFoundException {
    public FulfillmentOrderNotFoundException() {
        super("Fulfillment order ");
    }
}
