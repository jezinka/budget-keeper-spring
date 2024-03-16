package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.MoneyAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MoneyAmountRepository extends JpaRepository<MoneyAmount, Long> {

    Optional<MoneyAmount> findFirstByDateOrderByCreatedAtDesc(LocalDate date);

    Optional<MoneyAmount> findFirstByDate(LocalDate date);
}
