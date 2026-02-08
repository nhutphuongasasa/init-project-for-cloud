package com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.fallback;

import org.springframework.stereotype.Component;

import com.cloud.vendor_service.application.dto.response.InventoryResponse;
import com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.client.InventoryClient;

@Component
public class InventoryClientFallback implements InventoryClient {
    @Override
    public InventoryResponse getInventoryById(Long id) {
        return null;
    }
}
