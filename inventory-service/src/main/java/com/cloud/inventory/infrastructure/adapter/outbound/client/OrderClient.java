package com.cloud.inventory.infrastructure.adapter.outbound.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cloud.inventory.application.dto.request.OrphanCheckResult;
import com.cloud.inventory.infrastructure.config.FeignClientConfig;

@FeignClient(name = "order-service", configuration = FeignClientConfig.class)
public interface OrderClient {
    @PostMapping("/cleanupOrphanReserved")
    List<OrphanCheckResult> pushCheckListOrphanReserved(@RequestBody List<OrphanCheckResult> checkList);
}
