package com.example.budgetkeeperspring.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("logs")
public class LogController {

    @Autowired
    LogRepository logRepository;

    @GetMapping("")
    List<Log> getAll() {
        return logRepository.findAll();
    }

    @GetMapping("/{type}")
    List<Log> getByType(@PathVariable String type) {
        return logRepository.findAllByType(type);
    }
}
