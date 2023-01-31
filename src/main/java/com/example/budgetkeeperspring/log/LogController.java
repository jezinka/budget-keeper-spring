package com.example.budgetkeeperspring.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/active")
    List<Log> getAllActive() {
        return logRepository.findByDeletedIsFalseOrderByDateDesc();
    }

    @DeleteMapping("/{id}")
    void deleteLog(@PathVariable Long id) {
        logRepository.deleteById(id);
    }
}
