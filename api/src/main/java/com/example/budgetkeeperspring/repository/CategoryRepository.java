package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select distinct e.category from Expense e where function('year', e.transactionDate) = :year order by e.category.name")
    List<Category> findActiveForYear(@Param("year") int year);

    Category findCategoryByName(String s);
}
