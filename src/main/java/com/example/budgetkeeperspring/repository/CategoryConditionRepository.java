package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.CategoryCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryConditionRepository extends JpaRepository<CategoryCondition, Long> {
}
