package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
}
