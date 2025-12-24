package com.cloud.product_service.infrastructure.adapter.outbound.openfeign.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cloud.product_service.application.dto.response.StockSummaryDto;
import com.cloud.product_service.infrastructure.config.FeignClientConfig;

@FeignClient(name = "inventory-service", configuration = FeignClientConfig.class)
public interface InventoryClient {

    @PostMapping("/inventory-query/stock")
    Map<UUID, List<StockSummaryDto>> getStockByVariantIds(@RequestBody List<UUID> variantIds);
}
