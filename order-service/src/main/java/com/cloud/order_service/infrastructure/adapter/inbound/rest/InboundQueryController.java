package com.cloud.order_service.infrastructure.adapter.inbound.rest;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.service.InboundQueryService;
import com.cloud.order_service.common.response.FormResponse;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders-query")
public class InboundQueryController {
    private final InboundQueryService inboundQueryService;

      @GetMapping("/my-inbound")
    public ResponseEntity<FormResponse<Page<InboundOrderResponse>>> getMyInboundOrders(
        @PageableDefault Pageable pageable
    ) {
        Page<InboundOrderResponse> response = inboundQueryService.getMyInboundOrders(pageable);
        return ResponseEntity.ok(FormResponse.<Page<InboundOrderResponse>>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

    @GetMapping("/inbound")
    public ResponseEntity<FormResponse<Page<InboundOrderResponse>>> getInboundOrders(
        @PageableDefault Pageable pageable
    ) {
        Page<InboundOrderResponse> response = inboundQueryService.getInboundOrders(pageable);
        return ResponseEntity.ok(FormResponse.<Page<InboundOrderResponse>>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

    @GetMapping("/my-inbound/{id}")
    public ResponseEntity<FormResponse<InboundOrderResponse>> getMyInboundOrderDetail(
        @PathVariable UUID id
    ) {
        InboundOrderResponse response = inboundQueryService.getMyInboundOrder(id);
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

    @GetMapping("/inbound/{id}")
    public ResponseEntity<FormResponse<InboundOrderResponse>> getInboundOrderDetail(
        @PathVariable UUID id
    ) {
        InboundOrderResponse response = inboundQueryService.getAnyInboundOrderDetail(id);
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }
}