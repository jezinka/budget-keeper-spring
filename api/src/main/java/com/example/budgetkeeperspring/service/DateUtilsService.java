package com.example.budgetkeeperspring.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class DateUtilsService {

    public static LocalDate getBeginOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getEndOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }
}
