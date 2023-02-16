package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Liability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiabilityRepository extends JpaRepository<Liability, Long> {

    @Override
    @Query("select l from Liability l join fetch l.bank")
    List<Liability> findAll();
}
