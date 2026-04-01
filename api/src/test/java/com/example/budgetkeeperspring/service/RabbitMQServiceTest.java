package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.dto.PurchaseInfoDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Log;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LogRepository logRepository;

    @Mock
    private LogMapper logMapper;

    @InjectMocks
    private RabbitMQService rabbitMQService;

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Test
    @DisplayName("Should save expense when valid expense message is received")
    void listenExpenses_savesExpense() {
        String messageJson = "{\"title\":\"Test Expense\",\"payee\":\"Test Payee\"}";
        ExpenseDTO expenseDTO = gson.fromJson(messageJson, ExpenseDTO.class);
        Category category = new Category();
        when(categoryService.findCategoryByConditions(any(ExpenseDTO.class))).thenReturn(category);
        when(expenseService.createExpense(any(ExpenseDTO.class), any(Category.class))).thenReturn(expenseDTO);

        rabbitMQService.listenExpenses(messageJson);

        verify(expenseService, times(1)).createExpense(any(ExpenseDTO.class), any(Category.class));
    }

    @Test
    @DisplayName("Should save log when valid log message is received")
    void listenLogs_savesLog() {
        String messageJson = "{\"message\":\"Test Log\"}";
        when(logMapper.mapToEntity(any(LogDTO.class))).thenReturn(new Log());

        rabbitMQService.listenLogs(messageJson);

        verify(logRepository, times(1)).save(any(Log.class));
    }

    @Test
    @DisplayName("Should match purchase info to expense when valid purchase_info message is received")
    void listenPurchaseInfo_matchesExpense() {
        String messageJson = "{\"price\":\"45.0\",\"name\":\"KARMA DLA PTAKÓW 10kg\",\"orderDate\":\"9.02.2026, 09:02\",\"sendDate\":\"2026-03-31 17:48:36\"}";

        rabbitMQService.listenPurchaseInfo(messageJson);

        verify(expenseService, times(1)).matchPurchaseInfo(any(PurchaseInfoDTO.class));
    }
}