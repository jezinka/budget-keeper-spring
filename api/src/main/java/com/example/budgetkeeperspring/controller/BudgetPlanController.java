package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.BudgetPlanDTO;
import com.example.budgetkeeperspring.dto.BudgetPlanSummaryDTO;
import com.example.budgetkeeperspring.service.BudgetPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.example.budgetkeeperspring.utils.DateUtils.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.utils.DateUtils.getEndOfCurrentMonth;

@RestController
@RequestMapping("/budgetPlan")
public class BudgetPlanController {

    private final BudgetPlanService budgetPlanService;

    @Autowired
    public BudgetPlanController(BudgetPlanService budgetPlanService) {
        this.budgetPlanService = budgetPlanService;
    }

    @GetMapping
    public List<BudgetPlanDTO> getBudgetPlan() {
        LocalDate startDate = getBeginOfCurrentMonth();
        LocalDate endDate = getEndOfCurrentMonth();
        return budgetPlanService.getBudgetPlan(startDate, endDate);
    }

    @GetMapping("/summary")
    public BudgetPlanSummaryDTO getBudgetPlanSummary() {
        LocalDate startDate = getBeginOfCurrentMonth();
        LocalDate endDate = getEndOfCurrentMonth();
        return budgetPlanService.getBudgetPlanSummary(startDate, endDate);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }

        if (!budgetPlanService.createGoalsFromFile(file)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File uploaded failed.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + file.getOriginalFilename());
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportFile() {
        String data = budgetPlanService.writeDataToCsv();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=budgetPlan.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}