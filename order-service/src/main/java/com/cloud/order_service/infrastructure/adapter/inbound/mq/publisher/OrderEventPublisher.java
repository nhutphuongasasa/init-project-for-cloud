package com.cloud.order_service.infrastructure.adapter.inbound.mq.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.cloud.order_service.application.dto.event.InboundCompletedEvent;
import com.cloud.order_service.application.dto.event.OrderApprovedEvent;
import com.cloud.order_service.application.dto.event.OrderCancelledEvent;
import com.cloud.order_service.application.dto.event.OrderShippedEvent;
import com.cloud.order_service.application.dto.request.OrderInboundRequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_APPROVED = "order.approved";
    private static final String TOPIC_SHIPPED = "order.shipped";
    private static final String TOPIC_CANCELLED = "order.cancelled";
    private static final String TOPIC_ORDER_INBOUND = "order.inbound";

    public void publishApprovedEvent(OrderApprovedEvent event) {
        send(TOPIC_APPROVED, event.getOrderId().toString(), event);
    }

    //xuat khi thi relesae
    public void publishShippedEvent(OrderShippedEvent event) {
        send(TOPIC_SHIPPED, event.getOrderId().toString(), event);
    }

    public void publishCancelledEvent(OrderCancelledEvent event) {
        send(TOPIC_CANCELLED, event.getOrderId().toString(), event);
    }

    public void publishOrderInboundEvent(InboundCompletedEvent event) {
        send(TOPIC_ORDER_INBOUND, event.getOrderInboundId().toString(), event);
    }

    private void send(@NonNull String topic, @NonNull String key, @NonNull Object payload) {
        try {
            kafkaTemplate.send(topic, key, payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent event to {} | key={} | offset={}", 
                            topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send event to {} | key={}", topic, key, ex);
                        // TODO: DLQ hoặc retry mechanism
                    }
                });
        } catch (Exception e) {
            log.error("Exception when sending to Kafka topic {}", topic, e);
            throw e;
        }
    }
}