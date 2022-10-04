package com.example.budgetkeeperspring.groupedExpenses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupedExpenses {
    int month;
    String category;
    Float amount;
}
