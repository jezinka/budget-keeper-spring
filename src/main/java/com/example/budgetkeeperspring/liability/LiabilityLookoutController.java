package com.example.budgetkeeperspring.liability;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("liabilityLookouts")
public class LiabilityLookoutController {

    private final LiabilityLookoutRepository liabilityLookoutRepository;

    public LiabilityLookoutController(LiabilityLookoutRepository liabilityLookoutRepository) {
        this.liabilityLookoutRepository = liabilityLookoutRepository;
    }

    @PutMapping("")
    Boolean add(@RequestBody LiabilityLookout liabilityLookout) {
        liabilityLookoutRepository.save(liabilityLookout);
        return true;
    }

    @GetMapping("")
    List<LiabilityLookout> getLatest() {
        return liabilityLookoutRepository.retrieveAll().stream()
                .collect(Collectors.toMap(LiabilityLookout::getLiability,
                        Function.identity(),
                        (LiabilityLookout d1, LiabilityLookout d2) -> d1.getDate().after(d2.getDate()) ? d1 : d2))
                .values().stream().toList();
    }

    @GetMapping("/getLookouts/{id}")
    List<LiabilityLookout> getLookoutsForLiability(@PathVariable Long id) {
        return liabilityLookoutRepository.findAllByLiability_Id(id);
    }
}
