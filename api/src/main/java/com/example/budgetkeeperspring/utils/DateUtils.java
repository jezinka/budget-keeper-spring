package com.example.budgetkeeperspring.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

@Component
public class DateUtils {

    public static LocalDate getBeginOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getEndOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getBeginOfSelectedYear(Integer year){
        return LocalDate.of(year, Month.JANUARY, 1);
    }

    public static LocalDate getEndOfSelectedYear(Integer year){
        return LocalDate.of(year, Month.DECEMBER, 31);
    }
}
