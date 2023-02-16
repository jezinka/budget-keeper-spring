package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.MoneyAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MoneyAmountRepository extends JpaRepository<MoneyAmount, LocalDate> {

    MoneyAmount findFirstByDate(LocalDate date);
}
