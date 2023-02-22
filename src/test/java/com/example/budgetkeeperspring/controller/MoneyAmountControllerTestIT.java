package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.MoneyAmountDTO;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import com.example.budgetkeeperspring.repository.MoneyAmountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class MoneyAmountControllerTestIT {

    @Autowired
    MoneyAmountController moneyAmountController;

    @Autowired
    MoneyAmountRepository moneyAmountRepository;

    @Test
    void moneyAmount_create() {
        moneyAmountRepository.findAll();
        MoneyAmountDTO moneyAmountDTO = MoneyAmountDTO.builder()
                .amount(BigDecimal.valueOf(500))
                .build();

        ResponseEntity<MoneyAmountDTO> responseEntity = moneyAmountController.addMoneyAmountForCurrentMonth(moneyAmountDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationID = responseEntity.getHeaders().getLocation().getPath().split("/");
        Long savedId = Long.valueOf(locationID[2]);

        MoneyAmount moneyAmount = moneyAmountRepository.findById(savedId).get();
        assertThat(moneyAmount).isNotNull();

    }
}