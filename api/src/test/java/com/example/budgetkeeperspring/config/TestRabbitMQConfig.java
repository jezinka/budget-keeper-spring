package com.example.budgetkeeperspring.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestRabbitMQConfig {

    @Bean
    @Primary
    public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory() {
        return Mockito.mock(org.springframework.amqp.rabbit.connection.ConnectionFactory.class);
    }
}

