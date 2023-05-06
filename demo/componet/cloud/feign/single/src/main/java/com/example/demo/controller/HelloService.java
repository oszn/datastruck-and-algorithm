package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloService {
    @GetMapping(value = "/hello/say")
    public String sayHi(String name) {
        return "hello" + name;
    }

    ;
}
