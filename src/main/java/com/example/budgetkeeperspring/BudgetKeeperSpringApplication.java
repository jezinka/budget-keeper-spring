package com.example.budgetkeeperspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BudgetKeeperSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetKeeperSpringApplication.class, args);
    }

}
