package com.example.budgetkeeperspring.liability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LiabilityRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    List<Liability> getAll() {
        return jdbcTemplate.query("select * from latest_liability_value;", BeanPropertyRowMapper.newInstance(Liability.class));
    }

    public boolean addLookout(Long id, LiabilityLookout lookout) {
        int result = jdbcTemplate.update("insert into liability_lookout(date, outcome, liability_id) " +
                        "values (?, ?, ?)",
                lookout.date, lookout.outcome, id);
        return result == 1;
    }

    public List<LiabilityLookout> getLookouts(Long id) {
        return jdbcTemplate.query("select id, date, outcome from liability_lookout where liability_id = ?",
                BeanPropertyRowMapper.newInstance(LiabilityLookout.class), id);
    }
}
