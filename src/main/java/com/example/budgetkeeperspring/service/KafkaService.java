package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaService {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final LogRepository logRepository;
    private final LogMapper logMapper;
    private final FixedCostService fixedCostService;

    @KafkaListener(id = "springExpenseListener", topics = "expense")
    public void listenExpenses(String in) {
        Gson g = new Gson();
        ExpenseDTO message = g.fromJson(in, ExpenseDTO.class);
        Category category = categoryService.findCategoryByConditions(message);
        ExpenseDTO savedExpense = expenseService.createExpense(message, category);
        if (savedExpense != null) {
            fixedCostService.updateFixedCost(savedExpense);
        }
    }

    @KafkaListener(id = "springLogListener", topics = "log")
    public void listenLogs(String in) {
        Gson g = new Gson();
        LogDTO log = g.fromJson(in, LogDTO.class);
        logRepository.save(logMapper.mapToEntity(log));
    }
}
