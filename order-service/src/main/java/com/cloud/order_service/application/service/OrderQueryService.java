package com.cloud.order_service.application.service;

import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.dto.request.SearchOrderRequest;
import com.cloud.order_service.application.dto.response.OrderResponse;
import com.cloud.order_service.application.dto.response.OrderSummaryResponse;
import com.cloud.order_service.application.exception.FulfillmentOrderNotFoundException;
import com.cloud.order_service.application.mapper.FulfillmentOrderMapper;
import com.cloud.order_service.application.mapper.InboundOrderMapper;
import com.cloud.order_service.common.utils.jwt.JwtUtils;
import com.cloud.order_service.domain.model.FulfillmentOrder;
import com.cloud.order_service.domain.model.InboundOrder;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.FulfillmentOrderRepository;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.InboundOrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final FulfillmentOrderRepository orderRepo;
    private final FulfillmentOrderMapper mapper;
    private final JwtUtils jwtUtils;
    private final InboundOrderRepository inboundOrderRepo;
    private final InboundOrderMapper inboundOrderMapper;

    public Page<OrderSummaryResponse> getMyOrders(Pageable pageable) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            Sort.by("createdAt").descending()
        );
        Page<FulfillmentOrder> page = orderRepo.findAllByVendorId(vendorId, pageRequest);
        return page.map(mapper::toSummaryResponse);
    }

    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            Sort.by("createdAt").descending()
        );
        Page<FulfillmentOrder> page = orderRepo.findAll(pageRequest);
        return page.map(mapper::toSummaryResponse);
    }

    public OrderResponse getMyOrderDetail(UUID orderId) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        FulfillmentOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        if (!order.getVendorId().equals(vendorId)) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này");
        }
        return mapper.toResponse(order);
    }

    public OrderResponse getAnyOrder(UUID orderId) {
        FulfillmentOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        return mapper.toResponse(order);
    }

    public InboundOrderResponse getInboundOrder(UUID orderId) {
        InboundOrder order = inboundOrderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        return inboundOrderMapper.toResponse(order);
    }

    public InboundOrderResponse getAnyInboundOrderDetail(UUID orderId) {
        InboundOrder order = inboundOrderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        return inboundOrderMapper.toResponse(order);
    }

    public InboundOrderResponse getMyInboundOrder(UUID orderId) {
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
    

    public Page<OrderSummaryResponse> searchOrdersFilterTime(Pageable pageable, SearchOrderRequest request) {
        PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            Sort.by("createdAt").descending()
        );
        Instant startDate = request.getStartDate() != null
                ? request.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
                : null;

        Instant endDate = request.getEndDate() != null
                ? request.getEndDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
                : null;

        Specification<FulfillmentOrder> spec  = Specification.where(
            (root, query, cb) -> {
                if (startDate != null && endDate != null){
                    return cb.between(root.get("createdAt"), startDate, endDate);
                }else if(startDate != null){
                    return cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
                }else if (endDate != null){
                    return cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
                }

                return null;
            }
        );

        Page<FulfillmentOrder> page = orderRepo.findAll(spec, pageRequest);
        return page.map(mapper::toSummaryResponse);
    }

}