package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.service.LogService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.budgetkeeperspring.controller.LogController.LOG_PATH;

@Data
@RequiredArgsConstructor
@RestController
@RequestMapping(LOG_PATH)
public class LogController {

    public static final String LOG_PATH = "/logs";
    private final LogService logService;

    @GetMapping()
    List<LogDTO> getAll() {
        return logService.getAll();
    }

    @GetMapping("/forDisplay")
    LogDTO getErrorOrLastActive() {
        return logService.getErrorOrLastActive();
    }

    @GetMapping("/active")
    List<LogDTO> getAllActive() {
        return logService.getAllActive();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteLog(@PathVariable Long id) {
       logService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    LogDTO getLogById(@PathVariable("id") Long id) {
        return logService.getLogById(id);
    }
}
