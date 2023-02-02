package com.example.budgetkeeperspring.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("logs")
public class LogController {

    @Autowired
    LogRepository logRepository;

    @GetMapping("")
    List<Log> getAll() {
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @GetMapping("/active")
    List<Log> getAllActive() {
        return logRepository.findByDeletedIsFalse(Sort.by(Sort.Direction.DESC, "date"));
    }

    @DeleteMapping("/{id}")
    void deleteLog(@PathVariable Long id) {
        logRepository.deleteById(id);
    }
}
