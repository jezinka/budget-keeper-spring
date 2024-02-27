package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.FixedCostDTO;
import com.example.budgetkeeperspring.service.FixedCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.budgetkeeperspring.controller.FixedCostController.FIXED_COST;


@RequiredArgsConstructor
@RestController
@RequestMapping(FIXED_COST)
public class FixedCostController {

    public static final String FIXED_COST = "/fixedCost";

    private final FixedCostService fixedCostService;

    @GetMapping()
    List<FixedCostDTO> getAllForCurrentMonth() {
        return fixedCostService.getAllForCurrentMonth();
    }

    @PutMapping({"/{id}"})
    ResponseEntity updateFixedCost(@PathVariable("id") Long id) {
        fixedCostService.updateFixedCost(id);
        return ResponseEntity.noContent().build();
    }
}
