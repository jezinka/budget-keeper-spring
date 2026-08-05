package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.BeneficiaryDTO;
import com.example.budgetkeeperspring.service.BeneficiaryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/beneficiaries")
@Tag(name = "Beneficiary", description = "Beneficiaries who receives")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping("/all")
    List<BeneficiaryDTO> getAllBeneficiaries() {
        return beneficiaryService.getAllBeneficiaries();
    }
}
