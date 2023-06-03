package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.FixedCostPayed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FixedCostPayedRepository extends JpaRepository<FixedCostPayed, Long> {

    @Query("select fcp from FixedCostPayed fcp where fcp.payDate between :begin and :end")
    List<FixedCostPayed> findAllByFixedCostPayedBetween(@Param("begin") LocalDate begin, @Param("end") LocalDate end);
}
