package com.cloud.order_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.order_service.application.dto.request.CompleteReceivingRequest;
import com.cloud.order_service.application.dto.request.CreateInboundRequest;
import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.service.InboundCommandService;
import com.cloud.order_service.common.response.FormResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/1/14 12:02h
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders-command")
public class InboundCommandController {
    private final InboundCommandService inboundCommandService;

      @PostMapping("/inbound")
    public ResponseEntity<FormResponse<InboundOrderResponse>> createInbound(
        @Valid @RequestBody CreateInboundRequest request
    ) {
        InboundOrderResponse response = inboundCommandService.createInbound(request);
        
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Inbound created successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/confirm-inbound")
    public ResponseEntity<FormResponse<InboundOrderResponse>> confirmInbound(
        @PathVariable UUID id
    ) {
        InboundOrderResponse response = inboundCommandService.confirmInbound(id);
        
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Inbound confirmed successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/start-receiving")
    public ResponseEntity<FormResponse<InboundOrderResponse>> startReceiving(
        @PathVariable UUID id
    ) {
        InboundOrderResponse response = inboundCommandService.startReceiving(id);
        
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Inbound started receiving successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/complete-receiving")
    public ResponseEntity<FormResponse<InboundOrderResponse>> completeReceiving(
        @PathVariable UUID id,
        @Valid @RequestBody CompleteReceivingRequest request
    ) {
        InboundOrderResponse response = inboundCommandService.completeReceiving(id, request);
        
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Inbound completed receiving successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/cancel-inbound")
    public ResponseEntity<FormResponse<InboundOrderResponse>> cancelInbound(
        @PathVariable UUID id
    ) {
        InboundOrderResponse response = inboundCommandService.cancelInbound(id);
        
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Inbound cancelled successfully")
                .timestamp(Instant.now())
                .build());
    }

}
