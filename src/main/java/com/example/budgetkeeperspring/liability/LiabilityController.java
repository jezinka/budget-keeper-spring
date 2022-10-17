package com.example.budgetkeeperspring.liability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("liabilities")
public class LiabilityController {

    @Autowired
    LiabilityRepository liabilityRepository;

    @GetMapping("")
    List<Liability> getAll() {
        return liabilityRepository.getAll();
    }

    @PutMapping("/{id}")
    Boolean add(@PathVariable Long id, @RequestBody LiabilityLookout lookout) {
        return liabilityRepository.addLookout(id, lookout);
    }
}
