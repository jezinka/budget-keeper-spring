package com.example.budgetkeeperspring.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyExpenses {

    private Integer day;
    private Float amount;
}
