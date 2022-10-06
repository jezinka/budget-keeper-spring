package com.example.budgetkeeperspring.liability;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Liability {
    Long id;
    String name;
    String bank;
    Date date;
    Float outcome;
}
