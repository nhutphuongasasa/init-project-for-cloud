package com.cloud.order_service.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(
            java.util.Map.of("bootstrap.servers", "kafka-apache:9092")
        );
    }

    @Bean
    public NewTopic orderApprovedTopic() {
        return new NewTopic("order.approved", 3, (short) 1);
    }

    @Bean
    public NewTopic orderShippedTopic() {
        return new NewTopic("order.shipped", 3, (short) 1);
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return new NewTopic("order.cancelled", 3, (short) 1);
    }

    @Bean
    public NewTopic orderInbound() {
        return new NewTopic("order.inbound", 3, (short) 1);
    }
}
