package com.example.budgetkeeperspring.config;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMQConfig {

    /**
     * Retry up to 5 times with exponential backoff (1s, 2s, 4s, 8s, 16s).
     * After all retries are exhausted, RejectAndDontRequeueRecoverer sends the message to DLQ.
     * Queue topology (purchase_info → purchase_info.dlx → purchase_info.dlq) is defined in
     * init/definitions.json.
     */
    @Bean
    public RetryOperationsInterceptor purchaseInfoRetryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(5)
                .backOffOptions(1000, 2.0, 16000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory purchaseInfoListenerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor purchaseInfoRetryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(purchaseInfoRetryInterceptor);
        return factory;
    }
}
