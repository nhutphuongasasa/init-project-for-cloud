package com.cloud.plugin;

import org.keycloak.events.*;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

public class MyEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;
    private final RabbitMQSender sender;

    public MyEventListenerProvider(
        KeycloakSession session,
        RabbitMQSender sender
    ) {
        this.session = session;
        this.sender = sender;
    }

    @Override
    public void onEvent(Event event) {
        System.out.println(">>> [KEYCLOAK PLUGIN] Event: " + event.getType() + " | User: " + event.getUserId());

        if (event.getType() == EventType.LOGIN ||
            event.getType() == EventType.REGISTER ||
            event.getType() == EventType.LOGOUT) {
            try {
                String payload = String.format("{\"type\":\"%s\",\"userId\":\"%s\"}", 
                               event.getType().toString(), 
                               event.getUserId());
                sender.publish(
                    "user.exchange", 
                    "user." + event.getType().toString().toLowerCase(), 
                    payload
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void close() {
        try {
            sender.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}