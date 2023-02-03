package com.example.budgetkeeperspring.liability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiabilityLookoutRepository extends JpaRepository<LiabilityLookout, Long> {

    @Query("select lookout, bank from LiabilityLookout lookout join fetch lookout.liability join lookout.liability.bank as bank")
    List<LiabilityLookout> retrieveAll();

    List<LiabilityLookout> findAllByLiability_Id(Long id);
}
