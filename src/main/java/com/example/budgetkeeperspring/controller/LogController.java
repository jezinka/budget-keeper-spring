package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.entity.Log;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.budgetkeeperspring.controller.LogController.LOG_PATH;

@RequiredArgsConstructor
@RestController
@RequestMapping(LOG_PATH)
public class LogController {

    public static final String LOG_PATH = "/logs";

    private final LogRepository logRepository;

    @GetMapping()
    List<Log> getAll() {
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @GetMapping("/forDisplay")
    Log getErrorOrLastActive() {
        Log log = logRepository.findFirstByDeletedIsFalseAndTypeOrderByDateDesc("ERROR");
        if (log == null) {
            log = logRepository.findFirstByDeletedIsFalseAndTypeOrderByDateDesc("INFO");
        }
        return log;
    }

    @GetMapping("/active")
    List<Log> getAllActive() {
        return logRepository.findByDeletedIsFalse(Sort.by(Sort.Direction.DESC, "date"));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Log> deleteLog(@PathVariable Long id) {
        logRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    Log getLogById(@PathVariable("id") Long id) {
        return logRepository.findById(id).orElseThrow(NotFoundException::new);
    }
}
