package com.example.budgetkeeperspring.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LogRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    List<Log> getAll() {
        return jdbcTemplate.query("select id, date, type, message, is_deleted from log order by date desc;",
                BeanPropertyRowMapper.newInstance(Log.class));
    }

    List<Log> getAllActive() {
        return jdbcTemplate.query("select id, date, type, message from log " +
                        " where is_deleted != 1 " +
                        " order by date desc;",
                BeanPropertyRowMapper.newInstance(Log.class));
    }

    List<Log> findAllByType(String type) {
        return jdbcTemplate.query("select max(id), type, message from log where type=? " +
                        "group by type, message;",
                BeanPropertyRowMapper.newInstance(Log.class), type);
    }

    Boolean deleteLog(Long id) {
        int update = jdbcTemplate.update("update log set is_deleted = 1 where id = ?", id);
        return update == 1;
    }
}
