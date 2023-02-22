package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.MonthCategoryAmountDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ExpenseController {

    public static final String EXPENSES_PATH = "/expenses";
    public static final String EXPENSES_PATH_ID = EXPENSES_PATH + "/{id}";
    private final ExpenseService expenseService;

    @PostMapping(EXPENSES_PATH)
    List<Expense> getAllExpenses(@RequestBody HashMap<String, Object> filters) {
        return expenseService.findAll(filters);
    }

    @GetMapping(EXPENSES_PATH + "/currentMonth")
    List<ExpenseDTO> getCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @GetMapping(EXPENSES_PATH_ID)
    ExpenseDTO getById(@PathVariable Long id) {
        return expenseService.findById(id).orElseThrow(NotFoundException::new);
    }

    @DeleteMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> deleteExpense(@PathVariable Long id) {
        expenseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> editExpense(@PathVariable Long id, @RequestBody ExpenseDTO updateExpense) {
        Expense savedExpense = expenseService.updateExpense(id, updateExpense);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/expenses/" + savedExpense.getId());
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping(EXPENSES_PATH + "/split/{id}")
    ResponseEntity<ExpenseDTO> splitExpense(@PathVariable Long id, @RequestBody List<ExpenseDTO> expenseDTOS) {
        expenseService.splitExpense(id, expenseDTOS);
        return ResponseEntity.ok().build();
    }

    @GetMapping(EXPENSES_PATH + "/getPivot/{year}")
    List<Map<String, Object>> getForSelectedYearPivot(@PathVariable("year") Integer year) {
        return expenseService.getMonthsPivot(year);
    }

    @GetMapping(EXPENSES_PATH + "/yearAtTheGlance/{year}")
    Map<Integer, Map<String, BigDecimal>> getForSelectedYear(@PathVariable("year") Integer year) {
        return expenseService.getYearAtGlance(year);
    }

    @GetMapping(EXPENSES_PATH + "/currentMonthByCategory")
    List<MonthCategoryAmountDTO> getGroupedForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getGroupedByCategory(begin, end);
    }

    @GetMapping(EXPENSES_PATH + "/dailyExpenses")
    List<DailyExpensesDTO> getDailyForMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getDailyExpenses(begin, end);
    }
}
