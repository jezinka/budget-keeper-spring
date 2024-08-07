package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.MoneyAmount;
import com.example.budgetkeeperspring.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MoneyAmountRepositoryTest {

    @Autowired
    MoneyAmountRepository moneyAmountRepository;

    @Test
    void saveMoneyAmount_idTest() {

        MoneyAmount moneyAmount = moneyAmountRepository.save(MoneyAmount.builder()
                .amount(BigDecimal.valueOf(-234.21))
                .date(DateUtils.getBeginOfCurrentMonth())
                .build());

        assertThat(moneyAmount.getId()).isNotNull();
    }
}