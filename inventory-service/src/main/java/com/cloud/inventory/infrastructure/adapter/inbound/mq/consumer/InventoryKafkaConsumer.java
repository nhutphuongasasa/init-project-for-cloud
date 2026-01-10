package com.cloud.inventory.infrastructure.adapter.inbound.mq.consumer;

import com.cloud.inventory.application.kafka.event.OrderStockEvent;
import com.cloud.inventory.application.service.InventoryCommandService;
import com.cloud.inventory.domain.enums.ReferenceType;

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
    private final InventoryCommandService inventoryCommandService;

    @KafkaListener(
        topics = "order.approved",
        groupId = "inventory-service-group",
        concurrency = "3"
    )
    public void handleOrderApproved(OrderStockEvent event, Acknowledgment ack) {
        log.info("Received order approved event: {} – {} items",
                event.getOrderCode(), event.getItems().size());
                ack.acknowledge();

        try {
            inventoryCommandService.createOutBound(event, ReferenceType.ORDER_RESERVE);

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
    
    // public void handleOrderCancel(OrderCancelledEvent event, Acknowledgment ack) {
    //     log.info("Received order return event: {} – {} items",
    //             event.getOrderCode(), event.getItemsToRelease().size());

    //     try {
    //         event.getItemsToRelease().forEach(item -> {
    //             OrderApprovedEvent e = OrderApprovedEvent.builder()
    //                 .orderCode(event.getOrderCode())
    //                 .orderId(event.getOrderId())
    //                 .vendorId(event.getVendorId())
    //                 .warehouseId(event.getWarehouseId())
    //                 .items(event.getItemsToRelease())
    //                 .build();

    //             inventoryCommandService.createOutBound(e, ReferenceType.ORDER_RELEASE);

    //             ReturnProductRequest request = ReturnProductRequest

    //             inventoryCommandService.returnInventory(, null)
    //         });

    //         log.info("Successfully added stock for returned order: {}", event.getOrderCode());
    //         ack.acknowledge();
    //     } catch (Exception e) {
    //         log.error("Error adding stock for returned order {}: {}", event.getOrderCode(), e.getMessage(), e);
    //         throw e;
    //     }
    // }

    @KafkaListener(
        topics = "order.completePacking",
        groupId = "inventory-service-group",
        concurrency = "1"
    )
    public void handleOrderShipped(OrderStockEvent event, Acknowledgment ack) {
        log.info("Received order shipped event: {} – marking as shipped", event.getOrderId());

        try {
            log.info("Processing shipped event for order: {}", event.getOrderId());
            inventoryCommandService.createOutBound(event, ReferenceType.ORDER_RELEASE);

        } catch (Exception e) {
            log.error("Error processing order shipped event {}: {}", event.getOrderId(), e.getMessage(), e);
            throw e;
        }

        ack.acknowledge();
    }
}
