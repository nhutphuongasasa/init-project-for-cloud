package com.cloud.order_service.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.order_service.application.dto.event.OrderApprovedEvent;
import com.cloud.order_service.application.dto.event.OrderCancelledEvent;
import com.cloud.order_service.application.dto.event.OrderShippedEvent;
import com.cloud.order_service.application.dto.event.OutboundItem;
import com.cloud.order_service.application.dto.event.ReserveItem;
import com.cloud.order_service.application.dto.request.CancelOrderRequest;
import com.cloud.order_service.application.dto.request.CompletePickingRequest;
import com.cloud.order_service.application.dto.request.CreateOrderRequest;
import com.cloud.order_service.application.dto.request.UpdateQuantiryReceivedRequest;
import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.dto.response.OrderDetailResponse;
import com.cloud.order_service.application.dto.response.OrderResponse;
import com.cloud.order_service.application.exception.FulfillmentOrderNotFoundException;
import com.cloud.order_service.application.exception.InboundOrderDetailNotFoundException;
import com.cloud.order_service.application.exception.InvalidOrderCodeException;
import com.cloud.order_service.common.utils.jwt.JwtUtils;
import com.cloud.order_service.domain.enums.OrderStatus;
import com.cloud.order_service.domain.model.FulfillmentOrder;
import com.cloud.order_service.domain.model.FulfillmentOrderDetail;
import com.cloud.order_service.domain.model.InboundOrder;
import com.cloud.order_service.domain.model.InboundOrderDetail;
import com.cloud.order_service.infrastructure.adapter.inbound.mq.publisher.OrderEventPublisher;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.FulfillmentOrderDetailRepository;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.FulfillmentOrderRepository;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.InboundOrderDetailRepository;
import com.cloud.order_service.infrastructure.adapter.outbound.repository.InboundOrderRepository;
import com.cloud.order_service.infrastructure.mapper.FulfillmentOrderMapper;
import com.cloud.order_service.infrastructure.mapper.InboundOrderMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboundCommandService {
    private final FulfillmentOrderRepository orderRepo;
    private final FulfillmentOrderDetailRepository detailRepo;
    private final FulfillmentOrderMapper mapper;
    private final OrderEventPublisher eventPublisher;
    private final RedissonClient redisson;
    private final JwtUtils jwtUtils;
    private final InboundOrderMapper inboundMapper;
    private final InboundOrderRepository inboundRepo;
    private final InboundOrderDetailRepository inboundDetailRepo;

    private static final String LOCK_PREFIX = "order:lock:";

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        UUID vendorId = jwtUtils.getCurrentUserId();

        String orderCode = request.getExternalRef();
        if (orderCode == null || orderCode.trim().isEmpty()) {
            throw new InvalidOrderCodeException("Mã đơn sàn (external_ref) là bắt buộc");
        }
        if (orderRepo.existsByVendorIdAndOrderCodeAndStatusIn(
            vendorId, 
            orderCode, 
            List.of(
                OrderStatus.CREATED, 
                OrderStatus.APPROVED,
                OrderStatus.PICKING,
                OrderStatus.PICKED,
                OrderStatus.PACKING,
                OrderStatus.PACKED,
                OrderStatus.RETURNED
            ))) {
            throw new InvalidOrderCodeException("Đơn hàng " + orderCode + " đã tồn tại trong hệ thống của bạn");
        }

        FulfillmentOrder order = mapper.toEntity(request, vendorId);

        order.setOrderCode(orderCode);
        order.setVendorId(vendorId);
        order.setSource(request.getSource() != null && !request.getSource().isBlank() ? request.getSource() : "MANUAL");

        order.getItems().clear();
        request.getItems().forEach(itemReq -> {
            FulfillmentOrderDetail detail = mapper.toDetailEntity(itemReq, order);
            order.getItems().add(detail);
        });

        FulfillmentOrder saved = orderRepo.save(order);
        log.info("Vendor {} tạo đơn xuất kho: {}", vendorId, orderCode);
        return mapper.toResponse(saved);
    }

    @Transactional
    public OrderDetailResponse updatePickedQuantity(Long fulfillmentOrderDetailsId, int quantityPick) {
        FulfillmentOrderDetail detail = detailRepo.findById(fulfillmentOrderDetailsId)
            .orElseThrow(() -> new FulfillmentOrderNotFoundException());

        detail.setQuantityPicked(quantityPick);

        detailRepo.save(detail);

        return OrderDetailResponse.builder()
            .id(detail.getId())
            .productVariantId(detail.getProductVariantId())
            .productName(detail.getProductName())
            .quantityRequested(detail.getQuantityRequested())
            .quantityPicked(detail.getQuantityPicked())
            .unitPrice(detail.getUnitPrice())
            .notes(detail.getNotes())
            .build();
    }

    @Transactional
    public OrderResponse approveOrder(UUID orderId, UUID vendorId) {
        FulfillmentOrder order = getAndLock(orderId);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderCodeException("Chỉ duyệt được đơn ở trạng thái CREATED");
        }

        order.setStatus(OrderStatus.APPROVED);
        order.setUpdatedAt(Instant.now());

        FulfillmentOrder saved = orderRepo.save(order);
        OrderApprovedEvent event = OrderApprovedEvent.builder()
            .orderId(saved.getId())
            .orderCode(saved.getOrderCode())
            .warehouseId(saved.getWarehouseId())
            .vendorId(vendorId)
            .items(
                saved.getItems().stream()
                    .map(d -> ReserveItem.builder()
                        .productVariantId(d.getProductVariantId())
                        .quantity(d.getQuantityRequested())
                        
                        .build()
                    )
                    .collect(Collectors.toList())
            )
            .build();

        eventPublisher.publishApprovedEvent(event);

        return mapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse shipOrder(UUID orderId, UUID vendorId) {
        FulfillmentOrder order = getAndLock(orderId);

        if (order.getStatus() != OrderStatus.PACKED) {
            throw new InvalidOrderCodeException("Chỉ xuất kho khi đã PACKED");
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        FulfillmentOrder saved = orderRepo.save(order);

        List<OutboundItem> items = saved.getItems().stream()
            .map(d -> OutboundItem.builder()
                .productVariantId(d.getProductVariantId())
                .quantityPicked(
                    d.getQuantityPicked() != null && d.getQuantityPicked() > 0
                        ? d.getQuantityPicked()
                        : d.getQuantityRequested()
                )
                .build()
            )
            .collect(Collectors.toList());

        OrderShippedEvent event = OrderShippedEvent.builder()
            .orderId(saved.getId())
            .orderCode(saved.getOrderCode())
            .warehouseId(saved.getWarehouseId())
            .vendorId(vendorId)
            .items(items)
            .build();

        eventPublisher.publishShippedEvent(event);

        return mapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId, UUID vendorId, CancelOrderRequest request) {
        FulfillmentOrder order = getAndLock(orderId);

        if (List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED).contains(order.getStatus())) {
            throw new InvalidOrderCodeException("Không thể hủy đơn đã xuất kho hoặc đã hủy");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        // order.setCancelReason(request.getReason());

        FulfillmentOrder saved = orderRepo.save(order);

        if (order.getStatus() == OrderStatus.APPROVED) { 
            OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(saved.getId())
                .vendorId(vendorId)
                .warehouseId(saved.getWarehouseId())
                .itemsToRelease(saved.getItems().stream()
                    .map(d -> ReserveItem.builder()
                        .productVariantId(d.getProductVariantId())
                        .quantity(d.getQuantityRequested())
                        .build())
                    .toList())
                // .reason(request.getReason())
                .build();
            eventPublisher.publishCancelledEvent(event);
        }

        return mapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse completePicking(UUID orderId, CompletePickingRequest request) {
        RLock lock = redisson.getLock(LOCK_PREFIX + orderId);
        lock.lock(30, TimeUnit.SECONDS);

        try {
            FulfillmentOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());

            if (order.getStatus() != OrderStatus.PICKING) {
                throw new InvalidOrderCodeException("Phải ở trạng thái PICKING");
            }

            request.getItems().forEach(item -> {
                FulfillmentOrderDetail detail = detailRepo.findById(item.getDetailId())
                    .orElseThrow(() -> new FulfillmentOrderNotFoundException());
                detail.setQuantityPicked(item.getQuantityPicked());
                detail.setNotes(item.getNotes());
            });

            order.setStatus(OrderStatus.PICKED);
            order.setUpdatedAt(Instant.now());

            return mapper.toResponse(orderRepo.save(order));

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public OrderResponse startPicking(UUID orderId) {
        return changeStatusWithLock(orderId, OrderStatus.PICKING, o -> o.setPickedAt(Instant.now()));
    }

    @Transactional
    public OrderResponse startPacking(UUID orderId) {
        return changeStatusWithLock(orderId, OrderStatus.PACKING, o -> {
            // o.setPackingAt(Instant.now()); // nếu có field này thì bật lên
        });
    }

    @Transactional
    public OrderResponse completePacking(UUID orderId) {
        return changeStatusWithLock(orderId, OrderStatus.PACKED, o -> o.setPackedAt(Instant.now()));
    }

    //kiem tra lai
    @Transactional
    public OrderResponse changeStatusWithLock(UUID orderId, OrderStatus newStatus, Consumer<FulfillmentOrder> extraAction) {
        RLock lock = redisson.getLock(LOCK_PREFIX + orderId);
        lock.lock(30, TimeUnit.SECONDS);

        try {
            FulfillmentOrder order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new FulfillmentOrderNotFoundException());

            if (!isValidTransition(order.getStatus(), newStatus)) {
                throw new InvalidOrderCodeException(
                    "Chuyển trạng thái không hợp lệ: " + order.getStatus() + " → " + newStatus);
            }

            order.setStatus(newStatus);
            if (extraAction != null) {
                extraAction.accept(order);
            }
            order.setUpdatedAt(Instant.now());

            return mapper.toResponse(orderRepo.save(order));

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public InboundOrderResponse updateQuantityReceived(@NonNull Long inboundDetailId, UpdateQuantiryReceivedRequest request){
        InboundOrderDetail existInboundOrderDetail = inboundDetailRepo.findById(inboundDetailId)
            .orElseThrow(() -> new InboundOrderDetailNotFoundException());

        existInboundOrderDetail.setQuantityReceived(request.getQuantityReceived());

        InboundOrder saved = inboundRepo.save(existInboundOrderDetail.getInboundOrder());
        return inboundMapper.toResponse(saved);
    }

    private FulfillmentOrder getAndLock(UUID orderId) {
        RLock lock = redisson.getLock(LOCK_PREFIX + orderId);

        try {
            if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                throw new InvalidOrderCodeException("Đơn hàng đang được xử lý bởi người khác. Vui lòng thử lại sau ít phút.");
            }

            return orderRepo.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Hệ thống bận, vui lòng thử lại");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case CREATED -> to == OrderStatus.APPROVED || to == OrderStatus.CANCELLED;
            case APPROVED -> List.of(OrderStatus.PICKING, OrderStatus.CANCELLED).contains(to);
            case PICKING -> to == OrderStatus.PICKED;
            case PICKED -> to == OrderStatus.PACKING || to == OrderStatus.PICKING;
            case PACKING -> to == OrderStatus.PACKED;
            case PACKED -> to == OrderStatus.SHIPPED;
            default -> false;
        };
    }
}
