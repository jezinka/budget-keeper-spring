package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.MonthCategoryAmountDTO;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.budgetkeeperspring.service.DateUtilsService.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.service.DateUtilsService.getEndOfCurrentMonth;

@RequiredArgsConstructor
@RestController
public class ExpenseController {

    public static final String EXPENSES_PATH = "/expenses";
    public static final String EXPENSES_PATH_ID = EXPENSES_PATH + "/{id}";
    private final ExpenseService expenseService;

    @PostMapping(EXPENSES_PATH)
    List<ExpenseDTO> getAllExpenses(@RequestBody HashMap<String, Object> filters) {
        return expenseService.findAll(filters);
    }

    @GetMapping(EXPENSES_PATH + "/currentMonth")
    List<ExpenseDTO> getCurrentMonth() {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @GetMapping(EXPENSES_PATH_ID)
    ExpenseDTO getById(@PathVariable Long id) {
        return expenseService.findById(id).orElseThrow(NotFoundException::new);
    }

    @DeleteMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> deleteExpense(@PathVariable Long id) {
        if (!expenseService.deleteById(id)) {
            throw new NotFoundException();
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> editExpense(@PathVariable Long id, @Validated @RequestBody ExpenseDTO updateExpense) {
        if (expenseService.updateExpense(id, updateExpense).isEmpty()) {
            throw new NotFoundException();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(EXPENSES_PATH + "/split/{id}")
    ResponseEntity<ExpenseDTO> splitExpense(@PathVariable Long id, @Validated @RequestBody List<ExpenseDTO> expenseDTOS) {
        expenseService.splitExpense(id, expenseDTOS);
        return ResponseEntity.ok().build();
    }

    @GetMapping(EXPENSES_PATH + "/getPivot/{year}")
    List<Map<String, Object>> getForSelectedYearPivot(@PathVariable("year") Integer year) {
        return expenseService.getMonthsPivot(year);
    }

    @GetMapping(EXPENSES_PATH + "/yearAtTheGlance/{year}")
    List<MonthCategoryAmountDTO> getForSelectedYear(@PathVariable("year") Integer year) {
        return expenseService.getYearAtGlance(year);
    }

    @GetMapping(EXPENSES_PATH + "/currentMonthByCategory")
    List<MonthCategoryAmountDTO> getGroupedForCurrentMonth(@RequestParam(value = "withInvestments", required = false) boolean withInvestments) {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.getGroupedByCategory(begin, end, withInvestments);
    }

    @GetMapping(EXPENSES_PATH + "/dailyExpenses")
    List<DailyExpensesDTO> getDailyForMonth() {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.getDailyExpenses(begin, end);
    }
}
