package com.cloud.plugin;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQSender {
    private Connection connection;
    private Channel channel;

    public RabbitMQSender() {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(System.getenv("RABBITMQ_HOST"));
        factory.setUsername(System.getenv("RABBITMQ_USER"));
        factory.setPassword(System.getenv("RABBITMQ_PASS"));
        factory.setVirtualHost(System.getenv("RABBITMQ_VHOST"));
        factory.setPort(Integer.parseInt(System.getenv("RABBITMQ_PORT")));

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare("user.exchange", "topic", true);
            
            channel.queueDeclare("user.register.queue", true, false, false, null);
            channel.queueBind("user.register.queue", "user.exchange", "user.register");

            channel.queueDeclare("user.login.queue", true, false, false, null);
            channel.queueBind("user.login.queue", "user.exchange", "user.login");

            channel.queueDeclare("user.logout.queue", true, false, false, null);
            channel.queueBind("user.logout.queue", "user.exchange", "user.logout");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String exchange, String routingKey, String message) throws Exception {
        channel.basicPublish(exchange, routingKey, null, message.getBytes());
        System.out.println("[x] Sent " + message);
    }

    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}