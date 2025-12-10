package com.cloud.inventory.infrastructure.adapter.inbound.rest;

import java.util.UUID;
import java.time.Instant;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.inventory.application.service.InventoryCommandService;
import com.cloud.inventory.common.response.FormResponse;
import com.cloud.inventory.common.utils.jwt.JwtUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.cloud.inventory.application.dto.request.CreateInventoryItemRequest;
import com.cloud.inventory.application.dto.request.CreateStockMovementRequest;
import com.cloud.inventory.application.dto.request.TransferRequest;
import com.cloud.inventory.application.dto.response.StockMovementResponse;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory-command")
public class InventoryCommandController {

    private final InventoryCommandService service;
    private final JwtUtils jwtUtil;

    private UUID vendorId() {
        return jwtUtil.getCurrentUserId();
    }

    @PostMapping("/inbound")
    public FormResponse<StockMovementResponse> inbound(@Valid @RequestBody CreateInventoryItemRequest req) {
        StockMovementResponse data = service.createInbound(req, vendorId());
        return FormResponse.<StockMovementResponse>builder()
                .data(data)
                .message("Nhập kho thành công")
                .timestamp(Instant.now())
                .build();
    }

    @PostMapping("/adjust")
    public FormResponse<StockMovementResponse> adjust(@Valid @RequestBody CreateStockMovementRequest req) {
        StockMovementResponse data = service.adjustStock(req, vendorId());
        return FormResponse.<StockMovementResponse>builder()
                .data(data)
                .message("Điều chỉnh tồn kho thành công")
                .timestamp(Instant.now())
                .build();
    }

    @PostMapping("/transfer")
    public FormResponse<String> transfer(@Valid @RequestBody TransferRequest req) {
        String result = service.transferStock(req, vendorId());
        return FormResponse.<String>builder()
                .data(result)
                .message("Chuyển kho thành công")
                .timestamp(Instant.now())
                .build();
    }
}