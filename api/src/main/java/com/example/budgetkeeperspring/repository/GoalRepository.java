package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByDateBetween(@Param("begin") LocalDate begin, @Param("end") LocalDate end);

    List<Goal> findAllByDateBetweenAndCategory_LevelIsIn(@Param("begin") LocalDate begin, @Param("end") LocalDate end, @Param("level") List<Integer> levels);

    Optional<Goal> findGoalByCategoryAndDate(Category category, LocalDate date);
}
