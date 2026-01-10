package com.cloud.inventory.infrastructure.adapter.inbound.rest;

import java.time.Instant;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.inventory.application.service.InventoryCommandService;
import com.cloud.inventory.common.response.FormResponse;
import com.cloud.inventory.domain.enums.ReferenceType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.cloud.inventory.application.dto.request.AdjustStockRequest;
import com.cloud.inventory.application.dto.request.InboundRequest;
import com.cloud.inventory.application.dto.response.InventoryResponse;
import com.cloud.inventory.application.kafka.event.OrderStockEvent;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory-command")
public class InventoryCommandController {

    private final InventoryCommandService service;

    @PostMapping("/inbound/{type}")
    public FormResponse<Void> inbound(
            @Valid @RequestBody InboundRequest req,
            @PathVariable ReferenceType type 
        ) {
        service.createInbound(req, type);
        return FormResponse.<Void>builder()
                .data(null)
                .message("Nhập kho thành công")
                .timestamp(Instant.now())
                .build();
    }

    @PostMapping("/outbound/{type}")
    public FormResponse<Void> inbound(
            @Valid @RequestBody OrderStockEvent req,
            @PathVariable ReferenceType type 
        ) {
        service.createOutBound(req, type);
        return FormResponse.<Void>builder()
                .data(null)
                .message("Outbound suceesfully")
                .timestamp(Instant.now())
                .build();
    }

    @PostMapping("/adjust")
    public FormResponse<InventoryResponse> adjust(@Valid @RequestBody AdjustStockRequest req) {
        InventoryResponse data = service.adjustStock(req);
        return FormResponse.<InventoryResponse>builder()
                .data(data)
                .message("Điều chỉnh tồn kho thành công")
                .timestamp(Instant.now())
                .build();
    }    
}