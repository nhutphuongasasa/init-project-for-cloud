package com.cloud.vendor_service.infastructure.adapter.outbound.openfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cloud.vendor_service.application.dto.response.InventoryResponse;
import com.cloud.vendor_service.infastructure.config.FeignClientConfig;

@FeignClient(
    name = "inventory-service",
    url = "http://localhost:8083",
    configuration = FeignClientConfig.class
)
public interface InventoryClient {
    
    @GetMapping("/{id}")
    InventoryResponse getInventoryById(@PathVariable("id") Long id);
}
