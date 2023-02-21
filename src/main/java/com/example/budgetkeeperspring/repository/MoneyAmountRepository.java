package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.MoneyAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MoneyAmountRepository extends JpaRepository<MoneyAmount, LocalDate> {

    Optional<MoneyAmount> findFirstByDate(LocalDate date);
}
