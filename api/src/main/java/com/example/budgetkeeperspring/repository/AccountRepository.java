package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findBySinkingFundTrue();

    Account findByDefaultAccountTrue();
}
