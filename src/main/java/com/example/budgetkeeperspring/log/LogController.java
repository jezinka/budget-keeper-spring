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
        return logRepository.getAll();
    }

    @GetMapping("/active")
    List<Log> getAllActive() {
        return logRepository.getAllActive();
    }

    @GetMapping("/{type}")
    List<Log> getByType(@PathVariable String type) {
        return logRepository.findAllByType(type);
    }

    @DeleteMapping("/{id}")
    Boolean deleteLog(@PathVariable Long id) {
        return logRepository.deleteLog(id);
    }
}
