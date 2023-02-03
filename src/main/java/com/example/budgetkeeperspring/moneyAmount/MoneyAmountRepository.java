package com.example.budgetkeeperspring.moneyAmount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface MoneyAmountRepository extends JpaRepository<MoneyAmount, Date> {

    MoneyAmount findFirstByDate(Date date);
}
