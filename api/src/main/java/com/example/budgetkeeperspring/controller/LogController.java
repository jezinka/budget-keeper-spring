package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Logs", description = "Operations for managing application logs")
public class LogController {

    public static final String LOG_PATH = "/logs";
    private final LogService logService;

    @Operation(summary = "Get all logs")
    @GetMapping()
    List<LogDTO> getAll() {
        return logService.getAll();
    }

    @Operation(summary = "Get the latest error log or the last active log")
    @GetMapping("/forDisplay")
    LogDTO getErrorOrLastActive() {
        return logService.getErrorOrLastActive();
    }

    @Operation(summary = "Get all active logs")
    @GetMapping("/active")
    List<LogDTO> getAllActive() {
        return logService.getAllActive();
    }

    @Operation(summary = "Delete a log by ID")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteLog(@PathVariable Long id) {
       logService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a log by ID")
    @GetMapping("/{id}")
    LogDTO getLogById(@PathVariable("id") Long id) {
        return logService.getLogById(id);
    }
}
