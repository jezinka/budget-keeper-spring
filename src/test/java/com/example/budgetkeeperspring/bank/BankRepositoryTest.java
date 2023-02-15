package com.example.budgetkeeperspring.bank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

        assert repository.findAll().size() == 1;
    }

}