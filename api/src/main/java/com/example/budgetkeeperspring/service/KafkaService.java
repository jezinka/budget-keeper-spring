package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaService {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final LogRepository logRepository;
    private final LogMapper logMapper;
    private final FixedCostService fixedCostService;

    @KafkaListener(id = "springExpenseListener", topics = "expense", autoStartup = "${listen.auto.start:false}")
    public void listenExpenses(String in) {
        log.info("Received message: " + in);
        Gson g = new Gson();
        ExpenseDTO message = g.fromJson(in, ExpenseDTO.class);
        Optional<Category> category = categoryService.findCategoryByConditions(message);
        ExpenseDTO savedExpense = expenseService.createExpense(message, category);
        if (savedExpense != null) {
            fixedCostService.updateFixedCost(savedExpense);
        }
        log.info("Saved expense: " + savedExpense);
    }

    @KafkaListener(id = "springLogListener", topics = "log", autoStartup = "${listen.auto.start:false}")
    public void listenLogs(String in) {
        log.info("Received message: " + in);
        Gson g = new Gson();
        LogDTO logDto = g.fromJson(in, LogDTO.class);
        logRepository.save(logMapper.mapToEntity(logDto));
        log.info("Saved log: " + logDto);
    }
}
