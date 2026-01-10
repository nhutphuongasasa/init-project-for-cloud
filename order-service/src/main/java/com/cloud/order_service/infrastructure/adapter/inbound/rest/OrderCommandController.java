package com.cloud.order_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.order_service.application.dto.OrphanCheckResult;
import com.cloud.order_service.application.dto.request.ApproveOrderRequest;
import com.cloud.order_service.application.dto.request.CancelOrderRequest;
import com.cloud.order_service.application.dto.request.CompletePickingRequest;
import com.cloud.order_service.application.dto.request.CompleteReceivingRequest;
import com.cloud.order_service.application.dto.request.CreateInboundRequest;
import com.cloud.order_service.application.dto.request.CreateOrderRequest;
import com.cloud.order_service.application.dto.request.ShipOrderRequest;
import com.cloud.order_service.application.dto.request.UpdatePickedQuantityRequest;
import com.cloud.order_service.application.dto.request.UpdateQuantiryReceivedRequest;
import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.dto.response.OrderDetailResponse;
import com.cloud.order_service.application.dto.response.OrderResponse;
import com.cloud.order_service.application.service.OrderCommandService;
import com.cloud.order_service.common.response.FormResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders-command")
public class OrderCommandController {
    private final OrderCommandService orderCommandService;

    @PostMapping("/cleanupOrphanReserved")
    public ResponseEntity<FormResponse<List<OrphanCheckResult>>> checkListOrphanReserved(
        @RequestBody List<OrphanCheckResult> request
    ){
        List<OrphanCheckResult> result = orderCommandService.searchOrphanReserved(request);

        return ResponseEntity.ok(FormResponse.<List<OrphanCheckResult>>builder()
                    .data(result)
                    .message("check orphan sucessfully")
                    .timestamp(Instant.now())
                    .build()
        );
    }

    @PostMapping
    public ResponseEntity<FormResponse<OrderResponse>> createOrder(
        @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderCommandService.createOrder(request);
       
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order created successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/approve")
    public ResponseEntity<FormResponse<OrderResponse>> approveOrder(
        @RequestBody ApproveOrderRequest request
    ) {
        OrderResponse response = orderCommandService.approveOrder(request.getOrderId(), request.getVendorId());
        
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order approved successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/ship")
    public ResponseEntity<FormResponse<OrderResponse>> shipOrder(
        @RequestBody ShipOrderRequest request
    ) {
        OrderResponse response = orderCommandService.shipOrder(request.getOrderId(), request.getVendorId());
        
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order shipped successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FormResponse<InboundOrderResponse>> updateQuantityReceivedOrder(
        @PathVariable Long id,
        @RequestBody UpdateQuantiryReceivedRequest request
    ) {
        InboundOrderResponse response = orderCommandService.updateQuantityReceived(id, request);
        
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Order updated successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/cancel")
    public ResponseEntity<FormResponse<OrderResponse>> cancelOrder(
        @RequestBody CancelOrderRequest request
    ) {
        OrderResponse response = orderCommandService.cancelOrder(request.getOrderId(), request.getVendorId(), request);
        
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order cancelled successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PutMapping("/{id}/update-picked-quantity")
    public ResponseEntity<FormResponse<OrderDetailResponse>> updatePickedQuantity(
        @PathVariable Long id,
        @Valid @RequestBody UpdatePickedQuantityRequest request
    ) {
        OrderDetailResponse response = orderCommandService.updatePickedQuantity(id, request.getQuantityPick());
        
        return ResponseEntity.ok(FormResponse.<OrderDetailResponse>builder()
                .data(response)
                .message("Order updated picked quantity successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/start-picking")
    public ResponseEntity<FormResponse<OrderResponse>> startPicking(
        @PathVariable UUID id
    ) {
        OrderResponse response = orderCommandService.startPicking(id);
        
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order started picking successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/complete-picking")
    public ResponseEntity<FormResponse<OrderResponse>> completePicking(
        @PathVariable UUID id,
        @Valid @RequestBody CompletePickingRequest request
    ) {
        OrderResponse response = orderCommandService.completePicking(id, request);
        
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order completed picking successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/start-packing")
    public ResponseEntity<FormResponse<OrderResponse>> startPacking(
        @PathVariable UUID id
    ) {
        OrderResponse response = orderCommandService.startPacking(id);
        
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order started packing successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/complete-packing")
    public ResponseEntity<FormResponse<OrderResponse>> completePacking(
        @PathVariable UUID id
    ) {
        OrderResponse response = orderCommandService.completePacking(id);
        
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Order completed packing successfully")
                .timestamp(Instant.now())
                .build());
    }


    @PostMapping("/inbound")
    public ResponseEntity<FormResponse<InboundOrderResponse>> createInbound(
        @Valid @RequestBody CreateInboundRequest request
    ) {
        InboundOrderResponse response = orderCommandService.createInbound(request);
        
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
        InboundOrderResponse response = orderCommandService.confirmInbound(id);
        
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
        InboundOrderResponse response = orderCommandService.startReceiving(id);
        
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
        InboundOrderResponse response = orderCommandService.completeReceiving(id, request);
        
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
        InboundOrderResponse response = orderCommandService.cancelInbound(id);
        
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Inbound cancelled successfully")
                .timestamp(Instant.now())
                .build());
    }

    
}