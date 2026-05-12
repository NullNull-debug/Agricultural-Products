package com.farmproduct.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "message_exchange";
    public static final String USER_QUEUE = "user_message_queue";
    public static final String ADMIN_QUEUE = "admin_message_queue";
    public static final String USER_ROUTING_KEY = "user.#";
    public static final String ADMIN_ROUTING_KEY = "admin.#";

    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE).build();
    }

    @Bean
    public Queue adminQueue() {
        return QueueBuilder.durable(ADMIN_QUEUE).build();
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(userQueue).to(messageExchange).with(USER_ROUTING_KEY);
    }

    @Bean
    public Binding adminBinding(Queue adminQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(adminQueue).to(messageExchange).with(ADMIN_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}