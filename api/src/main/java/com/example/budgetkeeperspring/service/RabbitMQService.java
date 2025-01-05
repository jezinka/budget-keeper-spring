package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQService {

    @Value("${spring.rabbitmq.host}")
    private String address;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final LogRepository logRepository;
    private final LogMapper logMapper;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @RabbitListener(queues = "expense")
    public void listenExpenses(String in) {
        log.info("Received message: " + in);
        Gson g = new Gson();
        ExpenseDTO message = g.fromJson(in, ExpenseDTO.class);
        Category category = categoryService.findCategoryByConditions(message);
        ExpenseDTO savedExpense = expenseService.createExpense(message, category);
        log.info("Saved expense: " + savedExpense);
    }

    @RabbitListener(queues = "log")
    public void listenLogs(String in) {
        log.info("Received message: " + in);
        Gson g = new Gson();
        LogDTO logDto = g.fromJson(in, LogDTO.class);
        logRepository.save(logMapper.mapToEntity(logDto));
        log.info("Saved log: " + logDto);
    }
}
