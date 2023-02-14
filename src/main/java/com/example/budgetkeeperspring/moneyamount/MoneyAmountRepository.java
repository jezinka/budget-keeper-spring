package com.example.budgetkeeperspring.moneyamount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MoneyAmountRepository extends JpaRepository<MoneyAmount, LocalDate> {

    MoneyAmount findFirstByDate(LocalDate date);
}
