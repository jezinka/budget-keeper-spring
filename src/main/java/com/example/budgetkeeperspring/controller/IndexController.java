package com.example.budgetkeeperspring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("")
    String index() {
        return "Hello world!";
    }
}
