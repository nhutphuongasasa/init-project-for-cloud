package com.cloud.inventory.infrastructure.adapter.inbound.mq.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.inventory.application.kafka.event.FailedUpdateQuantityEvent;
import com.cloud.inventory.application.kafka.event.SuccessfulUpdateQuantityEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class InventoryKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String INSUFFICIENT_STOCK_TOPIC = "inventory.insufficientStock";
    private static final String PRODUCT_NOT_IN_WAREHOUSE = "inventory.productNotInWarehouse";
    private static final String INVENTORY_ERROR = "inventory.error";
    private static final String INVENTORY_RESERVE_SUCCESSFULLY = "inventory.reserve";
    private static final String INVENTORY_RELEASE_SUCCESSFULLY = "inventory.reserve";

    public void reservedOrderOutbound(SuccessfulUpdateQuantityEvent event) {
        sendMessage(INVENTORY_RESERVE_SUCCESSFULLY, event.getOrderId().toString(), log);
    }

    public void releaseOrderOutbound(SuccessfulUpdateQuantityEvent event) {
        sendMessage(INVENTORY_RELEASE_SUCCESSFULLY, event.getOrderId().toString(), log);
    }

    public void cancelledOrder(SuccessfulUpdateQuantityEvent event) {
        sendMessage(INVENTORY_RELEASE_SUCCESSFULLY, event.getOrderId().toString(), log);
    }

    public void failedInsufficientStock(FailedUpdateQuantityEvent event){
        sendMessage(INSUFFICIENT_STOCK_TOPIC, event.getOrderId().toString(), log);
    } 

    public void productNotInWarehouse(FailedUpdateQuantityEvent event){
        sendMessage(PRODUCT_NOT_IN_WAREHOUSE, event.getOrderId().toString(), log);  
    } 

    public void inventoryError(FailedUpdateQuantityEvent event){
        sendMessage(INVENTORY_ERROR, event.getOrderId().toString(), log);
    } 

    private void sendMessage(@NonNull String topic, @NonNull String key, @NonNull Object payload){
        try {
            kafkaTemplate.send(topic, key, payload)
                .whenComplete((result, ex) -> {
                    if(ex == null){
                        log.info("Sent message to {} | key key={} | offset={}", topic, key, result.getRecordMetadata().offset());
                    }else {
                        log.error("Failed to send event to {} | key={}", topic, key, ex);
                    }
                });
        } catch (Exception e) {
            log.error("Exception when sending to Kafka topic {}", topic, e);
            throw e;
        }
    }
}
