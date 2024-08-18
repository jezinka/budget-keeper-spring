package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
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

import java.util.Optional;

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

    @Mock
    private FixedCostService fixedCostService;

    @InjectMocks
    private RabbitMQService rabbitMQService;

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Test
    @DisplayName("Should save expense and update fixed cost when valid expense message is received")
    void listenExpenses_savesExpenseAndUpdatesFixedCost() {
        String messageJson = "{\"title\":\"Test Expense\",\"payee\":\"Test Payee\"}";
        ExpenseDTO expenseDTO = gson.fromJson(messageJson, ExpenseDTO.class);
        Category category = new Category();
        when(categoryService.findCategoryByConditions(any(ExpenseDTO.class))).thenReturn(Optional.of(category));
        when(expenseService.createExpense(any(ExpenseDTO.class), any(Optional.class))).thenReturn(expenseDTO);

        rabbitMQService.listenExpenses(messageJson);

        verify(expenseService, times(1)).createExpense(any(ExpenseDTO.class), any(Optional.class));
        verify(fixedCostService, times(1)).updateFixedCost(any(ExpenseDTO.class));
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
    @DisplayName("Should handle null category when expense message is received")
    void listenExpenses_handlesNullCategory() {
        String messageJson = "{\"title\":\"Test Expense\",\"payee\":\"Test Payee\"}";
        ExpenseDTO expenseDTO = gson.fromJson(messageJson, ExpenseDTO.class);
        when(categoryService.findCategoryByConditions(any(ExpenseDTO.class))).thenReturn(Optional.empty());
        when(expenseService.createExpense(any(ExpenseDTO.class), any(Optional.class))).thenReturn(expenseDTO);

        rabbitMQService.listenExpenses(messageJson);

        verify(expenseService, times(1)).createExpense(any(ExpenseDTO.class), any(Optional.class));
        verify(fixedCostService, times(1)).updateFixedCost(any(ExpenseDTO.class));
    }

    @Test
    @DisplayName("Should not update fixed cost when saved expense is null")
    void listenExpenses_doesNotUpdateFixedCostWhenExpenseIsNull() {
        String messageJson = "{\"title\":\"Test Expense\",\"payee\":\"Test Payee\"}";
        when(categoryService.findCategoryByConditions(any(ExpenseDTO.class))).thenReturn(Optional.empty());
        when(expenseService.createExpense(any(ExpenseDTO.class), any(Optional.class))).thenReturn(null);

        rabbitMQService.listenExpenses(messageJson);

        verify(fixedCostService, never()).updateFixedCost(any(ExpenseDTO.class));
    }
}