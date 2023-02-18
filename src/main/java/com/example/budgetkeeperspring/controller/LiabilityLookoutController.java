package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.LiabilityLookoutDTO;
import com.example.budgetkeeperspring.entity.LiabilityLookout;
import com.example.budgetkeeperspring.mapper.LiabilityLookoutMapper;
import com.example.budgetkeeperspring.repository.LiabilityLookoutRepository;
import com.example.budgetkeeperspring.service.LiabilityLookoutService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("liabilityLookouts")
public class LiabilityLookoutController {

    private final LiabilityLookoutRepository liabilityLookoutRepository;
    private final LiabilityLookoutService liabilityLookoutService;
    private final LiabilityLookoutMapper liabilityLookoutMapper;

    @PutMapping("")
    Boolean add(@RequestBody LiabilityLookoutDTO liabilityLookoutDTO) {
        liabilityLookoutRepository.save(liabilityLookoutMapper.mapToEntity(liabilityLookoutDTO));
        return true;
    }

    @GetMapping("")
    List<LiabilityLookoutDTO> getLatest() {
        return liabilityLookoutService.getLatest();
    }

    @GetMapping("/getLookouts/{id}")
    List<LiabilityLookout> getLookoutsForLiability(@PathVariable Long id) {
        return liabilityLookoutRepository.findAllByLiability_Id(id);
    }
}
