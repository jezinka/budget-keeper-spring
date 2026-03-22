package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.FireStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FireStageRepository extends JpaRepository<FireStage, Long> {

    List<FireStage> findAllByOrderByThresholdAsc();

    List<FireStage> findByFirstCrossedAtIsNullAndThresholdLessThanEqual(BigDecimal value);
}
