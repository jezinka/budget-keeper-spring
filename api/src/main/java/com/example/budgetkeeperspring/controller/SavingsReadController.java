package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.SavingsReadDTO;
import com.example.budgetkeeperspring.dto.SavingsReadGroupedDTO;
import com.example.budgetkeeperspring.service.SavingsReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/savingsRead")
public class SavingsReadController {

    private final SavingsReadService savingsReadService;

    @GetMapping("/latest")
    List<SavingsReadDTO> getLatestSavingsRead() {
        return savingsReadService.getLatest();
    }
    @GetMapping("/readAllGrouped")
    List<SavingsReadGroupedDTO> getAllReadsGrouped() {
        return savingsReadService.getAllGrouped();
    }
}
