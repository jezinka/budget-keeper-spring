package com.example.budgetkeeperspring.liability;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("liabilityLookouts")
public class LiabilityLookoutController {

    private final LiabilityLookoutRepository liabilityLookoutRepository;
    private final LiabilityLookoutService liabilityLookoutService;

    public LiabilityLookoutController(LiabilityLookoutRepository liabilityLookoutRepository, LiabilityLookoutService liabilityLookoutService) {
        this.liabilityLookoutRepository = liabilityLookoutRepository;
        this.liabilityLookoutService = liabilityLookoutService;
    }

    @PutMapping("")
    Boolean add(@RequestBody LiabilityLookout liabilityLookout) {
        liabilityLookoutRepository.save(liabilityLookout);
        return true;
    }

    @GetMapping("")
    List<LiabilityLookout> getLatest() {
        return liabilityLookoutService.getLatest();
    }

    @GetMapping("/getLookouts/{id}")
    List<LiabilityLookout> getLookoutsForLiability(@PathVariable Long id) {
        return liabilityLookoutRepository.findAllByLiability_Id(id);
    }
}
