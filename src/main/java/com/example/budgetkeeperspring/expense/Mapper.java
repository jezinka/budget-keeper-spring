package com.example.budgetkeeperspring.expense;

import com.example.budgetkeeperspring.category.Category;

import java.util.Objects;

public class Mapper {


    private static final Long EMPTY_OPTION = -1L;

    public static Expense mapFromDto(ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setTransactionDate(expenseDTO.getTransactionDate());
        expense.setTitle(expenseDTO.getTitle());
        expense.setPayee(expenseDTO.getPayee());
        expense.setAmount(expenseDTO.getAmount());
        if (!Objects.equals(expenseDTO.getCategoryId(), EMPTY_OPTION)) {
            Category category = new Category();
            category.setId(expenseDTO.getCategoryId());
            expense.setCategory(category);
        } else {
            expense.setCategory(null);
        }
        return expense;
    }
}
