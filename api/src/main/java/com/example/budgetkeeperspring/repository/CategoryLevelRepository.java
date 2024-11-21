package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.CategoryLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryLevelRepository extends JpaRepository<CategoryLevel, Long> {
    List<CategoryLevel> findAllByNameIn(List<String> name);
}
