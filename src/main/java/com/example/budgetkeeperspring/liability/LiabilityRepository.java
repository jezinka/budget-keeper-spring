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
        return jdbcTemplate.query("select l.id, l.name as name, b.name as bank, date, outcome " +
                "from liability_lookout ll " +
                "join liability l on ll.liability_id = l.id " +
                "join bank b on l.bank_id = b.id " +
                "where ll.id = (select ll2.id " +
                "               from liability_lookout ll2 " +
                "               where ll2.liability_id = ll.liability_id " +
                "               order by date desc " +
                "               limit 1)", BeanPropertyRowMapper.newInstance(Liability.class));
    }

    public boolean addLookout(Long id, LiabilityLookout lookout) {
        int result = jdbcTemplate.update("insert into liability_lookout(date, outcome, liability_id) " +
                        "values (?, ?, ?)",
                lookout.date, lookout.outcome, id);
        return result == 1;
    }
}
