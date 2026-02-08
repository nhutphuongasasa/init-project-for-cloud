package com.cloud.order_service.application.service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.order_service.application.dto.OrphanCheckResult;
import com.cloud.order_service.application.dto.request.SearchOrderRequest;
import com.cloud.order_service.application.dto.response.OrderResponse;
import com.cloud.order_service.application.dto.response.OrderSummaryResponse;
import com.cloud.order_service.application.exception.FulfillmentOrderNotFoundException;
import com.cloud.order_service.common.utils.jwt.JwtUtils;
import com.cloud.order_service.domain.model.FulfillmentOrder;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.FulfillmentOrderRepository;
import com.cloud.order_service.infrastructure.mapper.FulfillmentOrderMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/1/14 11:45h
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboundQueryService {
    private final FulfillmentOrderRepository orderRepo;
    private final FulfillmentOrderMapper mapper;
    private final JwtUtils jwtUtils;

    public List<OrphanCheckResult> searchOrphanReserved(List<OrphanCheckResult> checkList){
        List<Object[]> pairs = checkList.stream()
            .map(item -> new Object[]{
                item.getProductVariantId(), 
                item.getWarehouseId()
            })
            .toList();

        List<Object[]> rawResult = orderRepo.findPendingBatch(pairs);

        return rawResult.stream()
            .map(arr -> OrphanCheckResult.builder()
                            .productVariantId((UUID)arr[0])
                            .warehouseId((UUID)arr[1])
                            .build()
            ).toList();
    }

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

    public OrderResponse getMyOrderDetail(@NonNull UUID orderId) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        FulfillmentOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        if (!order.getVendorId().equals(vendorId)) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này");
        }
        return mapper.toResponse(order);
    }

    public OrderResponse getAnyOrder(@NonNull UUID orderId) {
        FulfillmentOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());
        return mapper.toResponse(order);
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
