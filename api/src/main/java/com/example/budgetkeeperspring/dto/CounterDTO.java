package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CounterDTO {

    private final long id;
    private final Integer count;
}
