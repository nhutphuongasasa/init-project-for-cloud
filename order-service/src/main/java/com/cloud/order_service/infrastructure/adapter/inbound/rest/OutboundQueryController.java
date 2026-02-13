package com.cloud.order_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.order_service.application.dto.OrphanCheckResult;
import com.cloud.order_service.application.dto.response.OrderResponse;
import com.cloud.order_service.application.dto.response.OrderSummaryResponse;
import com.cloud.order_service.application.service.OutboundQueryService;
import com.cloud.order_service.common.response.FormResponse;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders-query")
public class OutboundQueryController {
    private final OutboundQueryService outboundQueryService;

    @PostMapping("/cleanupOrphanReserved")
    public ResponseEntity<FormResponse<List<OrphanCheckResult>>> checkListOrphanReserved(
        @RequestBody List<OrphanCheckResult> request
    ){
        List<OrphanCheckResult> result = outboundQueryService.searchOrphanReserved(request);

        return ResponseEntity.ok(FormResponse.<List<OrphanCheckResult>>builder()
                    .data(result)
                    .message("check orphan sucessfully")
                    .timestamp(Instant.now())
                    .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormResponse<OrderResponse>> getOrderDetail(
        @PathVariable UUID id
    ) {
        OrderResponse response = outboundQueryService.getAnyOrder(id);
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
        Page<OrderSummaryResponse> response = outboundQueryService.getMyOrders(pageable);
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
        Page<OrderSummaryResponse> response = outboundQueryService.getAllOrders(pageable);
        return ResponseEntity.ok(FormResponse.<Page<OrderSummaryResponse>>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }


    @GetMapping("/my/{id}")
    public ResponseEntity<FormResponse<OrderResponse>> getMyOrderDetail(
        @PathVariable UUID id
    ) {
        OrderResponse response = outboundQueryService.getMyOrderDetail(id);
        return ResponseEntity.ok(FormResponse.<OrderResponse>builder()
                .data(response)
                .message("Success")
                .timestamp(java.time.Instant.now())
                .build());
    }
}
