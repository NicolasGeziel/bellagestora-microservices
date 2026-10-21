package com.bellagestora.auth_service.config.rabbitmq;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_USER_CREATED = "user.created.queue";
    @Bean
    public Queue userCreatedQueue(){
        return new Queue(QUEUE_USER_CREATED, true);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
