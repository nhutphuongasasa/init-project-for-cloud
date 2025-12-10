package com.cloud.vendor_service.infastructure.adapter.inbound.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class VendorEventConsumer {

    @RabbitListener(queues = "user.register.queue")
    public void handleRegister(String message) {
        System.out.println(">>> REGISTER event: " + message);
    }

    @RabbitListener(queues = "user.login.queue")
    public void handleLogin(String message) {
        System.out.println(">>> LOGIN event: " + message);
    }

    @RabbitListener(queues = "user.logout.queue")
    public void handleLogout(String message) {
        System.out.println(">>> LOGOUT event: " + message);
    }
}
