package com.example.budgetkeeperspring.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    //select distinct categoryId from Transaction where year(transactionDate) = :year
    @Query("select c from Category c where c.id IN (1,2,3) order by c.name")
    List<Category> findActiveForYear(@Param("year") int year);
}
