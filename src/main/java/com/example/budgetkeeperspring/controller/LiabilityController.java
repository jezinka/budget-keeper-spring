package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.entity.Liability;
import com.example.budgetkeeperspring.repository.LiabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("liabilities")
public class LiabilityController {

    private final LiabilityRepository liabilityRepository;

    @GetMapping("")
    List<Liability> getLiabilities() {
        return liabilityRepository.findAll();
    }
}
