package com.cloud.order_service.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.order_service.application.dto.OrphanCheckResult;
import com.cloud.order_service.application.dto.event.InboundCompletedEvent;
import com.cloud.order_service.application.dto.event.InboundItemEvent;
import com.cloud.order_service.application.dto.request.CompleteReceivingRequest;
import com.cloud.order_service.application.dto.request.CreateInboundRequest;
import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.exception.InvalidOrderCodeException;
import com.cloud.order_service.common.utils.jwt.JwtUtils;
import com.cloud.order_service.domain.enums.InboundStatus;
import com.cloud.order_service.domain.model.InboundOrder;
import com.cloud.order_service.domain.model.InboundOrderDetail;
import com.cloud.order_service.infrastructure.adapter.inbound.mq.publisher.OrderEventPublisher;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.FulfillmentOrderRepository;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.InboundOrderRepository;
import com.cloud.order_service.infrastructure.mapper.InboundOrderMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @since 2026/1/14 11:15h
 * @version 1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundCommandService {
    private final FulfillmentOrderRepository orderRepo;
    private final OrderEventPublisher eventPublisher;
    private final RedissonClient redisson;
    private final JwtUtils jwtUtils;
    private final InboundOrderMapper inboundMapper;
    private final InboundOrderRepository inboundRepo;

    private static final String INBOUND_LOCK = "inbound:lock:";

    @Transactional
    public InboundOrderResponse createInbound(CreateInboundRequest request) {
        UUID vendorId = jwtUtils.getCurrentUserId();
        String code = generateInboundCode(vendorId);

        if (inboundRepo.existsByVendorIdAndInboundCode(vendorId, code)) {
            throw new InvalidOrderCodeException("Phiếu nhập " + code + " đã tồn tại");
        }

        InboundOrder order = inboundMapper.toEntity(request, vendorId);
        order.setInboundCode(code);
        order.setVendorId(vendorId);
        order.setCreatedBy(vendorId);

        order.getItems().clear();
        request.getItems().forEach(itemReq -> {
            InboundOrderDetail detail = inboundMapper.toDetailEntity(itemReq, order);
            order.getItems().add(detail);
        });

        InboundOrder saved = inboundRepo.save(order);

        log.info("Tạo phiếu nhập kho: {}", code);
        return inboundMapper.toResponse(saved);
    }

    @Transactional
    public InboundOrderResponse confirmInbound(UUID inboundId) {
        return changeInboundStatus(inboundId, InboundStatus.CONFIRMED, null);
    }

    @Transactional
    public InboundOrderResponse startReceiving(UUID inboundId) {
        return changeInboundStatus(inboundId, InboundStatus.RECEIVING, null);
    }

    @Transactional
    public InboundOrderResponse completeReceiving(UUID inboundId, CompleteReceivingRequest request) {
        return changeInboundStatus(inboundId, InboundStatus.RECEIVED, order -> {
            request.getItems().forEach(item -> {
                InboundOrderDetail detail = order.getItems().stream()
                    .filter(d -> d.getId().equals(item.getDetailId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Chi tiết nhập kho không tồn tại"));
                detail.setQuantityReceived(item.getQuantityReceived());
                detail.setNotes(item.getNotes());
            });
            order.setReceivedAt(Instant.now());


        InboundCompletedEvent event = InboundCompletedEvent.builder()
            .inboundCode(order.getInboundCode())
            .vendorId(order.getVendorId())
            .orderInboundId(order.getId())
            .warehouseId(order.getWarehouseId())
            .items(order.getItems().stream()
                .map(d -> InboundItemEvent.builder()
                    .productVariantId(d.getProductVariantId())
                    .quantityReceived(d.getQuantityReceived())   // sửa cho khớp với field
                    .build())
                .toList())
            .build();

        eventPublisher.publishOrderInboundEvent(event);
            
        });
    }

    @Transactional
    public InboundOrderResponse cancelInbound(UUID inboundId) {
        return changeInboundStatus(inboundId, InboundStatus.CANCELLED, null);
    }

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

    private InboundOrderResponse changeInboundStatus(UUID inboundId, InboundStatus newStatus, Consumer<InboundOrder> extra) {
        RLock lock = redisson.getLock(INBOUND_LOCK + inboundId);
        lock.lock(30, TimeUnit.SECONDS);
        try {
            InboundOrder order = inboundRepo.findByIdWithItems(inboundId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập kho"));
            if (!isValidInboundTransition(order.getStatus(), newStatus)) {
                throw new InvalidOrderCodeException("Trạng thái nhập kho không hợp lệ: " + order.getStatus() + " → " + newStatus);
            }

            order.setStatus(newStatus);
            if (extra != null) extra.accept(order);

            return inboundMapper.toResponse(inboundRepo.save(order));
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    private boolean isValidInboundTransition(InboundStatus from, InboundStatus to) {
        return switch (from) {
            case DRAFT -> to == InboundStatus.CONFIRMED || to == InboundStatus.CANCELLED;
            case CONFIRMED -> to == InboundStatus.RECEIVING || to == InboundStatus.CANCELLED;
            case RECEIVING -> to == InboundStatus.RECEIVED;
            default -> false;
        };
    }

    private String generateInboundCode(UUID vendorId) {

        String prefix = "IN";
        String datePart = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));

        String vendorPart = vendorId.toString().replace("-", "").substring(0, 6).toUpperCase();

        int randomNum = (int) (Math.random() * 10000);
        String randomPart = String.format("%04d", randomNum);

        return prefix + datePart + vendorPart + randomPart;
    }
}
