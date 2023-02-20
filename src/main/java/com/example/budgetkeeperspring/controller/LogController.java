package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.entity.Log;
import com.example.budgetkeeperspring.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("logs")
public class LogController {

    private final LogRepository logRepository;

    @GetMapping("")
    List<Log> getAll() {
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @GetMapping("/active")
    List<Log> getAllActive() {
        return logRepository.findByDeletedIsFalse(Sort.by(Sort.Direction.DESC, "date"));
    }

    @DeleteMapping("/{id}")
    ResponseEntity deleteLog(@PathVariable Long id) {
        logRepository.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
