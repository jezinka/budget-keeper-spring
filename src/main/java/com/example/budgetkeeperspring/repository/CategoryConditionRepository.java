package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.CategoryCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryConditionRepository extends JpaRepository<CategoryCondition, Long> {

    @Query("SELECT cc, c FROM CategoryCondition cc JOIN FETCH cc.circ c")
    List<CategoryCondition> findAll();
}
