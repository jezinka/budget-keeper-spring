package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {

    List<PortfolioSnapshot> findAllByOrderByDateAsc();

    Optional<PortfolioSnapshot> findByDate(LocalDate date);
}
