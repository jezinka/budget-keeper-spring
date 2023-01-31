package com.example.budgetkeeperspring.moneyAmount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentMonthMoneyAmountRepository extends JpaRepository<CurrentMonthMoneyAmount, Long> {
}
