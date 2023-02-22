package com.example.budgetkeeperspring.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BigDecimalJsonSerializerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(doubles = {-12.123, -10, 2, 10.99})
    public void testSerialization(double number) throws JsonProcessingException {
        Map<String, BigDecimal> price = Map.of("price", BigDecimal.valueOf(number));
        String json = objectMapper.writeValueAsString(price);

        assertEquals("{\"price\":" + BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_UP) + "}", json);
    }
}