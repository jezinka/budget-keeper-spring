package com.example.budgetkeeperspring.liability;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("liabilities")
public class LiabilityController {

    private final LiabilityRepository liabilityRepository;

    public LiabilityController(LiabilityRepository liabilityRepository) {
        this.liabilityRepository = liabilityRepository;
    }

    @GetMapping("")
    List<Liability> getLiabilities() {
        return liabilityRepository.findAll();
    }
}
