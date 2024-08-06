package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByDateBetween(@Param("begin") LocalDate begin, @Param("end") LocalDate end);
}
