package com.example.budgetkeeperspring.log;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
@AllArgsConstructor
public class LogRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    List<Log> getAll() {
        return jdbcTemplate.query("select id, date, type, message from log;",
                BeanPropertyRowMapper.newInstance(Log.class));
    }

    List<Log> findAllByType(String type) {
        return jdbcTemplate.query("select id, date, type, message from log where type=?;",
                BeanPropertyRowMapper.newInstance(Log.class), type);
    }

    ;
}
