package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByAccountIdOrderByStartedAtDesc(Long accountId);

    List<AccountTransaction> findByAccountIdAndStartedAtBetween(Long accountId, LocalDateTime from, LocalDateTime to);

    List<AccountTransaction> findByAccountIdInAndStartedAtBetween(Collection<Long> accountIds, LocalDateTime from, LocalDateTime to);
}
