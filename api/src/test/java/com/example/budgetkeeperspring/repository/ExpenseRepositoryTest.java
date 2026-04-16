package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    ExpenseRepository repository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void save_tooLongName_test() {

        assertThrows(ConstraintViolationException.class, () -> {
            Expense expense = repository.save(Expense.builder()
                    .amount(BigDecimal.TEN)
                    .title("test tdfjlasjdfkdsjkfjdsklfjkadsjfkdsjfkdsjfksdjkfdsklfkldsjfkdsjfkjakfjdksjfkdsjfklaskldjskfjdkslfjkldsjfkldsjfkdsjkfjdsklfjklajfdsjfasfitletdfjlasjdfkdsjkfjdsklfjkadsjfkdsjfkdsjfksdjkfdsklfkldsjfkdsjfkjakfjdksjfkdsjfklaskldjskfjdkslfjkldsjfkldsjfkdsjkfjdsklfjklajfdsjfasfitle")
                    .transactionDate(LocalDate.now())
                    .build());
            repository.flush();
        });
    }

    @Test
    void save_test() {
        Expense expense = repository.save(Expense.builder()
                .amount(BigDecimal.TEN)
                .title("test title")
                .transactionDate(LocalDate.now())
                .build());

        assertThat(expense.getId()).isNotNull();
    }

    @Test
    void retrieveAll_ShouldReturnOnlyUndeletedEntities() {

        repository.save(Expense.builder()
                .amount(BigDecimal.valueOf(-1.99))
                .transactionDate(LocalDate.of(2020, 12, 1))
                .deleted(true)
                .build());

        repository.save(Expense.builder()
                .amount(BigDecimal.valueOf(-1.99))
                .transactionDate(LocalDate.of(2020, 12, 1))
                .build());

        List<Expense> all = repository.findAllByOrderByTransactionDateDesc();
        assertEquals(1, all.size());
        assertTrue(all.stream().noneMatch(Expense::isDeleted));
    }

    @Test
    void findAllByCategoryNameIn_returnsMatchingExpenses() {
        Category biedronka = categoryRepository.save(new Category("Biedronka"));
        Category rachunki = categoryRepository.save(new Category("Rachunki"));
        Category inne = categoryRepository.save(new Category("Inne"));

        repository.save(Expense.builder().amount(BigDecimal.valueOf(-120)).transactionDate(LocalDate.of(2024, 1, 10)).category(biedronka).build());
        repository.save(Expense.builder().amount(BigDecimal.valueOf(-80)).transactionDate(LocalDate.of(2024, 2, 5)).category(rachunki).build());
        repository.save(Expense.builder().amount(BigDecimal.valueOf(-50)).transactionDate(LocalDate.of(2024, 3, 1)).category(inne).build());

        List<Expense> result = repository.findAllByCategoryNameIn(List.of("Biedronka", "Rachunki"));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> List.of("Biedronka", "Rachunki").contains(e.getCategory().getName()));
    }

    @Test
    void findAllByCategoryNameIn_emptyList_returnsNoExpenses() {
        Category cat = categoryRepository.save(new Category("Biedronka"));
        repository.save(Expense.builder().amount(BigDecimal.valueOf(-100)).transactionDate(LocalDate.now()).category(cat).build());

        List<Expense> result = repository.findAllByCategoryNameIn(List.of());

        assertThat(result).isEmpty();
    }
}