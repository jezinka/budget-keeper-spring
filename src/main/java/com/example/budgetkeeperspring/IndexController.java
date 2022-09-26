package com.example.budgetkeeperspring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("")
    String index() {
        return "Hello world!";
    }
}
