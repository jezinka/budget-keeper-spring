package com.example.budgetkeeperspring.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private static final Long EMPTY_OPTION = -1L;
    @Autowired
    ExpenseRepository expenseRepository;

    Boolean updateTransaction(Long id, Expense updateExpense) {
        Expense expense = expenseRepository.getById(id);
        if (updateExpense != null) {
            setTransactionProperties(updateExpense, expense);
        }
        expenseRepository.save(expense);
        return true;
    }

    Boolean splitExpanse(Long id, List<Expense> updateExpenses) {
        try {
            Expense expense = expenseRepository.getById(id);
            for (Expense e : updateExpenses) {
                setTransactionProperties(expense, e);
                expenseRepository.save(e);
            }
            expenseRepository.deleteById(id);
            return true;

        } catch (DataIntegrityViolationException exception) {
            return false;
        }
    }

    private static void setTransactionProperties(Expense updateExpense, Expense expense) {
        expense.setTransactionDate(updateExpense.getTransactionDate());
        expense.setTitle(updateExpense.getTitle());
        expense.setPayee(updateExpense.getPayee());
        expense.setAmount(updateExpense.getAmount());
        if (updateExpense.getCategoryId() != EMPTY_OPTION) {
            expense.setCategoryId(updateExpense.getCategoryId());
        }
        if (updateExpense.getLiabilityId() != EMPTY_OPTION) {
            expense.setLiabilityId(updateExpense.getLiabilityId());
        }
    }

    public Map getDailyExpenses(LocalDate begin, LocalDate end) {
        List<Expense> expenses = expenseRepository.findAllByTransactionDateBetween(Date.valueOf(begin), Date.valueOf(end));

        return expenses.stream()
                .filter(e -> e.getAmount() < 0)
                .collect(Collectors.groupingBy(Expense::getTransactionDay,
                        Collectors.summingDouble(e -> Math.abs(e.getAmount()))));
    }

    public List<Expense> findAll(HashMap filters) {
        return expenseRepository.findAll();
//       String query = "select * from transactions_category " +
//                "where 1=1  ";
//
//        if (filters.getOrDefault("onlyEmptyCategories", false).equals(true)) {
//            query += " and category_id is null ";
//        }
//        if (filters.getOrDefault("onlyEmptyLiabilities", false).equals(true)) {
//            query += " and liability_id is null ";
//        }
//        if (filters.getOrDefault("onlyExpenses", false).equals(true)) {
//            query += " and amount < 0 ";
//        }
//        if (filters.get("year") != null) {
//            query += " and year(transaction_date) = " + filters.get("year");
//        }
//        if (filters.get("month") != null) {
//            query += " and month(transaction_date) = " + filters.get("month");
//        }
//        if (filters.get("category") != null) {
//            query += " and category = \"" + filters.get("category") + "\"";
//        }
//
//        String titleFilter = (String) filters.getOrDefault("title", "");
//        if (!titleFilter.equals("")) {
//            query += " and title like \"%" + titleFilter + "%\" ";
//        }
//
//        String payeeFilter = (String) filters.getOrDefault("payee", "");
//        if (!payeeFilter.equals("")) {
//            query += " and payee like \"%" + payeeFilter + "%\" ";
//        }
//
//        query += " order by transaction_date desc";
//
//        return jdbcTemplate.query(query, BeanPropertyRowMapper.newInstance(Expense.class));
    }
}
