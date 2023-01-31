package com.example.budgetkeeperspring.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where exists (select e from Expense e where function('year', e.transactionDate) = :year) order by c.name")
    List<Category> findActiveForYear(@Param("year") int year);
}
