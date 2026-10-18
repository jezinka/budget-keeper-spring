package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryCondition;
import com.example.budgetkeeperspring.entity.Circ;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
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
    private LogService logService;

    @InjectMocks
    private RabbitMQService rabbitMQService;

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Test
    @DisplayName("Should save expense and log its lifecycle when condition and plan match")
    void listenExpenses_logsAutoCategoryAndPlan() {
        String messageJson = "{\"title\":\"Netflix\",\"payee\":\"Netflix BV\",\"amount\":-43.0}";
        Category category = Category.builder().name("Rozrywka").build();
        Circ circ = new Circ();
        circ.setTitle("netflix");
        when(categoryService.findMatchingCondition(any(ExpenseDTO.class)))
                .thenReturn(Optional.of(CategoryCondition.builder().category(category).circ(circ).build()));
        when(expenseService.createExpenseFromQueue(any(ExpenseDTO.class), eq(category)))
                .thenReturn(ExpenseDTO.builder().id(7L).plannedExpenseId(3).build());

        rabbitMQService.listenExpenses(messageJson);

        verify(logService).expense(7L, LogService.INFO, "mail z banku: Netflix | Netflix BV | -43.0");
        verify(logService).expense(7L, LogService.INFO, "kategoria auto: Rozrywka (warunek tytuł~'netflix')");
        verify(logService).expense(7L, LogService.INFO, "plan auto: pozycja 'Netflix' (#3)");
    }

    @Test
    @DisplayName("Should warn when neither category nor plan matched")
    void listenExpenses_warnsWhenNothingMatched() {
        String messageJson = "{\"title\":\"Sklep\",\"payee\":\"X\",\"amount\":-5.0}";
        when(categoryService.findMatchingCondition(any(ExpenseDTO.class))).thenReturn(Optional.empty());
        when(categoryService.unknownCategory()).thenReturn(new Category());
        when(expenseService.createExpenseFromQueue(any(ExpenseDTO.class), any(Category.class)))
                .thenReturn(ExpenseDTO.builder().id(8L).build());

        rabbitMQService.listenExpenses(messageJson);

        verify(logService).expense(eq(8L), eq(LogService.WARN), startsWith("kategoria: żaden warunek"));
        verify(logService).expense(eq(8L), eq(LogService.WARN), startsWith("plan: brak pozycji o nazwie 'Sklep'"));
    }

    @Test
    @DisplayName("Should log error and rethrow when expense cannot be saved")
    void listenExpenses_logsErrorOnFailure() {
        String messageJson = "{\"title\":\"Sklep\",\"payee\":\"X\",\"amount\":-5.0}";
        when(categoryService.findMatchingCondition(any(ExpenseDTO.class))).thenThrow(new IllegalStateException("boom"));

        assertThrows(IllegalStateException.class, () -> rabbitMQService.listenExpenses(messageJson));

        verify(logService).expense(eq(null), eq(LogService.ERROR), endsWith("boom"));
    }

    @Test
    @DisplayName("Should save log when valid log message is received")
    void listenLogs_savesLog() {
        String messageJson = "{\"message\":\"Test Log\"}";
        when(logMapper.mapToEntity(any(LogDTO.class))).thenReturn(new Log());

        rabbitMQService.listenLogs(messageJson);

        verify(logRepository, times(1)).save(any(Log.class));
    }
}