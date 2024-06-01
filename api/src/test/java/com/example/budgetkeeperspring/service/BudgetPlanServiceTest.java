package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.BudgetPlanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetPlanServiceTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @InjectMocks
    private BudgetPlanService budgetPlanService;

    @BeforeEach
    void setUp() {
        BudgetPlanDTO budgetPlanDTO1 = new BudgetPlanDTO();
        budgetPlanDTO1.setId(1L);
        budgetPlanDTO1.setCategory("category1");
        budgetPlanDTO1.setExpense(new BigDecimal("100.00"));
        budgetPlanDTO1.setGoal(new BigDecimal("200.00"));

        BudgetPlanDTO budgetPlanDTO2 = new BudgetPlanDTO();
        budgetPlanDTO2.setId(2L);
        budgetPlanDTO2.setCategory("category2");
        budgetPlanDTO2.setExpense(new BigDecimal("300.00"));
        budgetPlanDTO2.setGoal(new BigDecimal("400.00"));

        List<BudgetPlanDTO> budgetPlanDTOList = Arrays.asList(budgetPlanDTO1, budgetPlanDTO2);

        when(namedParameterJdbcTemplate.query(any(String.class), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(budgetPlanDTOList);
    }

    @Test
    @DisplayName("Should return correct budget plan when getBudgetPlan is called")
    void getBudgetPlan() {
        List<BudgetPlanDTO> result = budgetPlanService.getBudgetPlan(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 7, 1));

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("category1", result.get(0).getCategory());
        assertEquals(new BigDecimal("100.00"), result.get(0).getExpense());
        assertEquals(new BigDecimal("200.00"), result.get(0).getGoal());
        assertEquals(2L, result.get(1).getId());
        assertEquals("category2", result.get(1).getCategory());
        assertEquals(new BigDecimal("300.00"), result.get(1).getExpense());
        assertEquals(new BigDecimal("400.00"), result.get(1).getGoal());
    }
}