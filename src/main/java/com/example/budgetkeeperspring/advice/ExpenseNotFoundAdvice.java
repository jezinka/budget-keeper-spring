package com.example.budgetkeeperspring.advice;

import com.example.budgetkeeperspring.exception.ExpenseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExpenseNotFoundAdvice {

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String expenseNotFoundHandler(ExpenseNotFoundException ex) {
        return ex.getMessage();
    }
}
