package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.CategoryLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryLevelRepository extends JpaRepository<CategoryLevel, Long> {
}
