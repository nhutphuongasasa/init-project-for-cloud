package com.cloud.order_service.infrastructure.adapter.inbound.rest;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.order_service.application.dto.request.SearchOrderRequest;
import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.dto.response.OrderResponse;
import com.cloud.order_service.application.dto.response.OrderSummaryResponse;
import com.cloud.order_service.application.service.OrderQueryService;
import com.cloud.order_service.common.response.FormResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders-query")
public class OrderQueryController {
    private final OrderQueryService orderQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<FormResponse<OrderResponse>> getOrderDetail(
        @PathVariable UUID id
    ) {
        OrderResponse response = orderQueryService.getAnyOrder(id);
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

    @GetMapping
    public ResponseEntity<FormResponse<Page<OrderSummaryResponse>>> getMyOrders(
        @PageableDefault Pageable pageable
    ) {
        Page<OrderSummaryResponse> response = orderQueryService.getMyOrders(pageable);
        return ResponseEntity.ok(FormResponse.<Page<OrderSummaryResponse>>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

    @GetMapping("/all")
    public ResponseEntity<FormResponse<Page<OrderSummaryResponse>>> getAllOrders(
        @PageableDefault Pageable pageable
    ) {
        Page<OrderSummaryResponse> response = orderQueryService.getAllOrders(pageable);
        return ResponseEntity.ok(FormResponse.<Page<OrderSummaryResponse>>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

    // @GetMapping("/admin/{id}")
    // public ResponseEntity<FormResponse<OrderResponse>> getAnyOrderDetail(
    //     @PathVariable UUID id
    // ) {
    //     OrderResponse response = orderQueryService.getAnyOrderDetail(id);
    //     return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
    //             .data(response)
    //             .message("Success")
    //             .timestamp(java.time.Instant.now())
    //             .build());
    // }

    @GetMapping("/my/{id}")
    public ResponseEntity<FormResponse<OrderResponse>> getMyOrderDetail(
        @PathVariable UUID id
    ) {
        OrderResponse response = orderQueryService.getMyOrderDetail(id);
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }



    @GetMapping("/my-inbound")
    public ResponseEntity<FormResponse<Page<InboundOrderResponse>>> getMyInboundOrders(
        @PageableDefault Pageable pageable
    ) {
        Page<InboundOrderResponse> response = orderQueryService.getMyInboundOrders(pageable);
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
        Page<InboundOrderResponse> response = orderQueryService.getInboundOrders(pageable);
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
        InboundOrderResponse response = orderQueryService.getMyInboundOrder(id);
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
        InboundOrderResponse response = orderQueryService.getAnyInboundOrderDetail(id);
        return ResponseEntity.ok(FormResponse.<InboundOrderResponse>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<FormResponse<Page<OrderSummaryResponse>>> searchOrders(
        @PageableDefault Pageable pageable,
        @Valid @RequestBody SearchOrderRequest request
    ) {
        Page<OrderSummaryResponse> response = orderQueryService.searchOrdersFilterTime(pageable, request);
        return ResponseEntity.ok(FormResponse.<Page<OrderSummaryResponse>>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }

}
