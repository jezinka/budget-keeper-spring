package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.dto.PurchaseInfoDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final LogRepository logRepository;
    private final LogMapper logMapper;

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

    @RabbitListener(queues = "purchase_info", containerFactory = "purchaseInfoListenerFactory")
    public void listenPurchaseInfo(String in) {
        log.info("Received purchase_info: " + in);
        Gson g = new Gson();
        PurchaseInfoDTO purchaseInfo = g.fromJson(in, PurchaseInfoDTO.class);
        boolean matched = expenseService.matchPurchaseInfo(purchaseInfo);
        if (!matched) {
            throw new RuntimeException("No matching expense yet for purchase: " + purchaseInfo.getName()
                    + ", price=" + purchaseInfo.getPrice()
                    + ", orderDate=" + purchaseInfo.getOrderDate());
        }
    }
}
