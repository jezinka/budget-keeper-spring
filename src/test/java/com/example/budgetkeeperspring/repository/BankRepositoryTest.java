package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Bank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BankRepositoryTest {

    @Autowired
    BankRepository repository;

    @Test
    public void findAllShouldReturnEntity() {

        Bank entity = new Bank();
        entity.setName("TestBank");
        repository.save(entity);

        assertEquals(1, repository.findAll().size());
    }

}