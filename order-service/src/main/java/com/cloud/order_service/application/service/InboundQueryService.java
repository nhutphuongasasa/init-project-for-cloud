package com.cloud.order_service.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.exception.FulfillmentOrderNotFoundException;
import com.cloud.order_service.common.utils.jwt.JwtUtils;
import com.cloud.order_service.domain.model.InboundOrder;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.InboundOrderRepository;
import com.cloud.order_service.infrastructure.mapper.InboundOrderMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundQueryService {
    private final JwtUtils jwtUtils;
    private final InboundOrderRepository inboundOrderRepo;
    private final InboundOrderMapper inboundOrderMapper;

    public InboundOrderResponse getInboundOrder(@NonNull UUID orderId) {
        InboundOrder order = inboundOrderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        return inboundOrderMapper.toResponse(order);
    }

    public InboundOrderResponse getAnyInboundOrderDetail(@NonNull UUID orderId) {
        InboundOrder order = inboundOrderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        return inboundOrderMapper.toResponse(order);
    }

    public InboundOrderResponse getMyInboundOrder(@NonNull UUID orderId) {
        InboundOrder order = inboundOrderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        UUID vendorId = jwtUtils.getCurrentUserId();
        if (!order.getVendorId().equals(vendorId)) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này");
        }
        return inboundOrderMapper.toResponse(order);
    }

    public Page<InboundOrderResponse> getMyInboundOrders(Pageable pageable) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            Sort.by("createdAt").descending()
        );
        Page<InboundOrder> page = inboundOrderRepo.findAllByVendorId(vendorId, pageRequest);
        return page.map(inboundOrderMapper::toResponse);
    }

    public Page<InboundOrderResponse> getInboundOrders(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            Sort.by("createdAt").descending()
        );
        Page<InboundOrder> page = inboundOrderRepo.findAll(pageRequest);
        return page.map(inboundOrderMapper::toResponse);
    }
}
