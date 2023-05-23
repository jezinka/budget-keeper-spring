package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.FixedCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FixedCostRepository extends JpaRepository<FixedCost, Long> {

    @Query("select fc from FixedCost fc left join fetch fc.fixedCostPayed fcp where (fcp.payDate between :begin and :end) or fcp.id is null")
    List<FixedCost> findAllByFixedCostPayedBetween(@Param("begin") LocalDate begin, @Param("end") LocalDate end);
}
