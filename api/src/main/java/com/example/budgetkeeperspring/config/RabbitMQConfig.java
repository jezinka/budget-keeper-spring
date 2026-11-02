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

    /**
     * The shop mail usually beats the bank mail by a few minutes (both inboxes are polled), so wait
     * ~18 min: 12 attempts, backoff 1s doubling up to 5 min. The listener thread blocks meanwhile;
     * other purchases just queue behind it.
     * After all retries are exhausted, RejectAndDontRequeueRecoverer sends the message to DLQ.
     * Queue topology (purchase_info → purchase_info.dlx → purchase_info.dlq) is defined in
     * init/definitions.json.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory purchaseInfoListenerFactory(ConnectionFactory connectionFactory, LogService logService) {
        return retryThenReject(connectionFactory, logService, "purchase_info niedopasowane", 12, 300_000);
    }

    /**
     * Same policy for bank mails, but short (5 attempts, up to 16s): transient failures get retried,
     * a broken message is rejected (DLQ if the queue has one, dropped otherwise) instead of being redelivered forever.
     * The ERROR log keeps the payload so the expense can be re-entered by hand.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory expenseListenerFactory(ConnectionFactory connectionFactory, LogService logService) {
        return retryThenReject(connectionFactory, logService, "mail z banku nieprzetworzony", 5, 16_000);
    }

    private static SimpleRabbitListenerContainerFactory retryThenReject(ConnectionFactory connectionFactory, LogService logService, String what,
                                                                      int maxAttempts, long maxBackoffMs) {
        RejectAndDontRequeueRecoverer reject = new RejectAndDontRequeueRecoverer();
        RetryOperationsInterceptor retry = RetryInterceptorBuilder.stateless()
                .maxAttempts(maxAttempts)
                .backOffOptions(1000, 2.0, maxBackoffMs)
                .recoverer((message, cause) -> {
                    Throwable root = cause.getCause() != null ? cause.getCause() : cause;
                    String body = new String(message.getBody(), StandardCharsets.UTF_8);
                    log.error("{} after {} attempts, rejecting: {}", what, maxAttempts, body, root);
                    // payload first: the row is cut at 255 chars
                    logService.expense(null, LogService.ERROR, "%s po %d próbach (odrzucone): %s | błąd: %s".formatted(
                            what, maxAttempts, body, root.getMessage()));
                    reject.recover(message, cause);
                })
                .build();
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(retry);
        return factory;
    }
}
