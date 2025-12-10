package com.cloud.inventory.infrastructure.adapter.inbound.mq.consumer;

import com.cloud.inventory.application.dto.request.StockMovementRecord;
import com.cloud.inventory.application.kafka.event.OrderApprovedEvent;
import com.cloud.inventory.application.kafka.event.OrderCancelledEvent;
import com.cloud.inventory.application.kafka.event.OrderInboundRequest;
import com.cloud.inventory.application.kafka.event.OrderShippedEvent;
import com.cloud.inventory.application.service.StockMovementService;
import com.cloud.inventory.domain.enums.StockMovementType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryKafkaConsumer {
    private final StockMovementService stockMovementService;

    @KafkaListener(
        topics = "order.approved",
        groupId = "inventory-service-group",
        concurrency = "3"
    )
    public void handleOrderApproved(OrderApprovedEvent event, Acknowledgment ack) {
        log.info("Received order approved event: {} – {} items",
                event.getOrderCode(), event.getItems().size());
                ack.acknowledge();

        try {
            event.getItems().forEach(item -> {
                stockMovementService.recordMovement(StockMovementRecord.builder()
                    .productVariantId(item.getProductVariantId())
                    .warehouseId(event.getWarehouseId())
                    .type(StockMovementType.OUTBOUND)
                    .quantity(item.getQuantity())
                    .referenceType("ORDER_RESERVE")
                    .notes("Automatic outbound – Order " + event.getOrderCode())
                    .build(), event.getVendorId());
            });

            log.info("Successfully deducted stock for order: {}", event.getOrderCode());
        } catch (Exception e) {
            log.error("Error deducting stock for order {}: {}", event.getOrderCode(), e.getMessage(), e);
            throw e; // Kafka will retry
        }
    }

    @KafkaListener(
        topics = "order.returned",
        groupId = "inventory-service-group",
        concurrency = "2"
    )
    public void handleOrderCannlled(OrderCancelledEvent event, Acknowledgment ack) {
        log.info("Received order return event: {} – {} items",
                event.getOrderCode(), event.getItemsToRelease().size());

        try {
            event.getItemsToRelease().forEach(item -> {
                stockMovementService.recordMovement(StockMovementRecord.builder()
                    .productVariantId(item.getProductVariantId())
                    .warehouseId(event.getWarehouseId())
                    .type(StockMovementType.OUTBOUND)
                    .quantity(item.getQuantity())
                    .referenceType("ORDER_RELEASE")
                    .notes("Automatic return – Order " + event.getOrderCode())
                    .build(), event.getVendorId());

                stockMovementService.recordMovement(StockMovementRecord.builder()
                    .productVariantId(item.getProductVariantId())
                    .warehouseId(event.getWarehouseId())
                    .type(StockMovementType.INBOUND)
                    .quantity(item.getQuantity())
                    .referenceType("RETURN")
                    .notes("Automatic return – Order " + event.getOrderCode())
                    .build(), event.getVendorId());
            });

            log.info("Successfully added stock for returned order: {}", event.getOrderCode());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error adding stock for returned order {}: {}", event.getOrderCode(), e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "order.shipped",
        groupId = "inventory-service-group",
        concurrency = "1"
    )
    public void handleOrderShipped(OrderShippedEvent event, Acknowledgment ack) {
        log.info("Received order shipped event: {} – marking as shipped", event.getOrderId());

        try {
            log.info("Processing shipped event for order: {}", event.getOrderId());
            event.getItems().forEach(item -> {
                log.info("Updating stock for item: {} in warehouse: {}", item.getProductVariantId(), event.getWarehouseId());
                stockMovementService.recordMovement(
                    StockMovementRecord.builder()
                        .productVariantId(item.getProductVariantId())
                        .warehouseId(event.getWarehouseId())
                        .type(StockMovementType.OUTBOUND)
                        .quantity(item.getQuantityPicked())
                        .referenceType("ORDER_RELEASE")
                        .notes("Order shipped – " + event.getOrderId())
                        .build(),
                    event.getVendorId()
                );
            });
        } catch (Exception e) {
            log.error("Error processing order shipped event {}: {}", event.getOrderId(), e.getMessage(), e);
            throw e;
        }

        ack.acknowledge();
    }

    @KafkaListener(
        topics = "order.inbound",
        groupId = "inventory-service-group",
        concurrency = "1"
    )
    public void handleOrderInboud(OrderInboundRequest event, Acknowledgment ack) {
        log.info("Received order inbound event: {} – marking as shipped", event.getOrderInboundId());

        try {
            log.info("Processing inbound event for order: {}", event.getOrderInboundId());
            event.getItems().forEach(item -> {
                log.info("Updating stock for item: {} in warehouse: {}", item.getProductVariantId(), event.getWarehouseId());
                stockMovementService.recordMovement(
                    StockMovementRecord.builder()
                        .productVariantId(item.getProductVariantId())
                        .warehouseId(event.getWarehouseId())
                        .type(StockMovementType.INBOUND)
                        .quantity(item.getQuantityReceived())
                        .referenceType("INBOUND")
                        .notes("Order inbound – " + event.getInboundCode())
                        .build(),
                    event.getVendorId()
                );
            });
        } catch (Exception e) {
            log.error("Error processing order shipped event {}: {}", event.getOrderInboundId(), e.getMessage(), e);
            throw e;
        }

        ack.acknowledge();
    }
}
