package com.example.budgetkeeperspring.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Category> getAll() {
        return jdbcTemplate.query("select id, name from category order by name", BeanPropertyRowMapper.newInstance(Category.class));
    }

    public List<Category> getActive(int year) {
        return jdbcTemplate.query("select id, name  " +
                "from category  " +
                "where id in (select distinct category_id from transaction where year(transaction_date) = ?)  " +
                "order by name;", BeanPropertyRowMapper.newInstance(Category.class), year);
    }
}
