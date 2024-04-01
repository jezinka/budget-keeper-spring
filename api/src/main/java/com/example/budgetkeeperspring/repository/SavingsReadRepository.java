package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.SavingsRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsReadRepository extends JpaRepository<SavingsRead, Long> {

    @Query("SELECT sr,s FROM SavingsRead sr JOIN FETCH sr.savings s")
    List<SavingsRead> findAll();
}
