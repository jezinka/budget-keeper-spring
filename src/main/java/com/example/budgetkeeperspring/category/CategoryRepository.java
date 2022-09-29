package com.example.budgetkeeperspring.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Repository
public class CategoryRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public List<Category> getAll() {
        return jdbcTemplate.query("select id, name from category", BeanPropertyRowMapper.newInstance(Category.class));
    }
}
