package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
