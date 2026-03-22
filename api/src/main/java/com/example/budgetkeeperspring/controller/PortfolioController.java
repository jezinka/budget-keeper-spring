package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.FireStageDTO;
import com.example.budgetkeeperspring.dto.PortfolioSnapshotDTO;
import com.example.budgetkeeperspring.service.FireStageService;
import com.example.budgetkeeperspring.service.PortfolioSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioSnapshotService portfolioSnapshotService;
    private final FireStageService fireStageService;

    @GetMapping("/snapshots")
    List<PortfolioSnapshotDTO> getSnapshots() {
        return portfolioSnapshotService.findAll();
    }

    @PostMapping("/snapshots")
    ResponseEntity<PortfolioSnapshotDTO> addSnapshot(@RequestBody PortfolioSnapshotDTO dto) {
        PortfolioSnapshotDTO saved = portfolioSnapshotService.save(dto.getDate(), dto.getValue(), dto.getInvestedCapital());
        if (saved == null) {
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.status(201).body(saved);
    }

    @PostMapping("/snapshots/fetch")
    ResponseEntity<Void> fetchNow() {
        portfolioSnapshotService.fetchFromMyFundAndSave();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fire-stages")
    List<FireStageDTO> getFireStages() {
        return fireStageService.findAll();
    }
}

