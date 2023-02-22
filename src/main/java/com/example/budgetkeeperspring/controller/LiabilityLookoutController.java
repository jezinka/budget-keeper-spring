package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.LiabilityLookoutDTO;
import com.example.budgetkeeperspring.entity.LiabilityLookout;
import com.example.budgetkeeperspring.mapper.LiabilityLookoutMapper;
import com.example.budgetkeeperspring.repository.LiabilityLookoutRepository;
import com.example.budgetkeeperspring.service.LiabilityLookoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("liabilityLookouts")
public class LiabilityLookoutController {

    private final LiabilityLookoutRepository liabilityLookoutRepository;
    private final LiabilityLookoutService liabilityLookoutService;
    private final LiabilityLookoutMapper liabilityLookoutMapper;

    @PostMapping("")
    ResponseEntity<LiabilityLookoutDTO> add(@RequestBody LiabilityLookoutDTO liabilityLookoutDTO) {
        LiabilityLookout savedLiabilityLookout = liabilityLookoutRepository.save(liabilityLookoutMapper.mapToEntity(liabilityLookoutDTO));
        return ResponseEntity.created(URI.create("/liabilityLookouts/" + savedLiabilityLookout.getId())).build();
    }

    @GetMapping("")
    List<LiabilityLookoutDTO> getLatest() {
        return liabilityLookoutService.getLatest();
    }

    @GetMapping("/getLookouts/{id}")
    List<LiabilityLookoutDTO> getLookoutsForLiability(@PathVariable("id") Long id) {
        return liabilityLookoutRepository.findAllByLiability_Id(id)
                .stream()
                .map(liabilityLookoutMapper::mapToDto)
                .toList();
    }
}
