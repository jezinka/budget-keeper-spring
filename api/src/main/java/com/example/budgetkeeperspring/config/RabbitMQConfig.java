package com.example.budgetkeeperspring.config;

import com.example.budgetkeeperspring.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.nio.charset.StandardCharsets;

@Slf4j
@Profile("!local")
@Configuration
public class RabbitMQConfig {

    private static final int MAX_ATTEMPTS = 5;

    /**
     * Retry up to 5 times with exponential backoff (1s, 2s, 4s, 8s, 16s).
     * After all retries are exhausted, RejectAndDontRequeueRecoverer sends the message to DLQ.
     * Queue topology (purchase_info → purchase_info.dlx → purchase_info.dlq) is defined in
     * init/definitions.json.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory purchaseInfoListenerFactory(ConnectionFactory connectionFactory, LogService logService) {
        return retryThenReject(connectionFactory, logService, "purchase_info niedopasowane");
    }

    /**
     * Same policy for bank mails: transient failures get retried, a broken message is rejected
     * (DLQ if the queue has one, dropped otherwise) instead of being redelivered forever.
     * The ERROR log keeps the payload so the expense can be re-entered by hand.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory expenseListenerFactory(ConnectionFactory connectionFactory, LogService logService) {
        return retryThenReject(connectionFactory, logService, "mail z banku nieprzetworzony");
    }

    private static SimpleRabbitListenerContainerFactory retryThenReject(ConnectionFactory connectionFactory, LogService logService, String what) {
        RejectAndDontRequeueRecoverer reject = new RejectAndDontRequeueRecoverer();
        RetryOperationsInterceptor retry = RetryInterceptorBuilder.stateless()
                .maxAttempts(MAX_ATTEMPTS)
                .backOffOptions(1000, 2.0, 16000)
                .recoverer((message, cause) -> {
                    Throwable root = cause.getCause() != null ? cause.getCause() : cause;
                    String body = new String(message.getBody(), StandardCharsets.UTF_8);
                    log.error("{} after {} attempts, rejecting: {}", what, MAX_ATTEMPTS, body, root);
                    // payload first: the row is cut at 255 chars
                    logService.expense(null, LogService.ERROR, "%s po %d próbach (odrzucone): %s | błąd: %s".formatted(
                            what, MAX_ATTEMPTS, body, root.getMessage()));
                    reject.recover(message, cause);
                })
                .build();
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(retry);
        return factory;
    }
}
