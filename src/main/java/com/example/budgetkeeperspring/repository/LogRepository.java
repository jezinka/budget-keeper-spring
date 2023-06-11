package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Log;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    Log findFirstByDeletedIsFalseAndTypeOrderByDateDesc(String type);

    List<Log> findByDeletedIsFalse(Sort sort);

    @Modifying
    @Query("delete from Log l where l.date < :before")
    void deleteAllByDateBefore(@Param("before") LocalDate date);
}
