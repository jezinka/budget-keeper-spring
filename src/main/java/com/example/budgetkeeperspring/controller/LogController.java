package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.entity.Log;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class LogController {

    public static final String LOG_PATH = "/logs";
    public static final String LOG_PATH_ID = LOG_PATH + "/{id}";

    private final LogRepository logRepository;

    @GetMapping(LOG_PATH)
    List<Log> getAll() {
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @GetMapping(LOG_PATH + "/active")
    List<Log> getAllActive() {
        return logRepository.findByDeletedIsFalse(Sort.by(Sort.Direction.DESC, "date"));
    }

    @DeleteMapping(LOG_PATH_ID)
    ResponseEntity deleteLog(@PathVariable Long id) {
        logRepository.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping(LOG_PATH_ID)
    Log getLogById(@PathVariable("id") Long id) {
        return logRepository.findById(id).orElseThrow(() -> new NotFoundException("Long with id: " + id + " not found"));
    }
}
