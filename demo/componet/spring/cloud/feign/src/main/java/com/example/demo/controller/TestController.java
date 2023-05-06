package com.example.demo.controller;

import com.example.demo.feign.HelloServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    HelloServiceFeign helloServiceFeign;

    @GetMapping(value = "/test/say")
    public String sayHi() {
        return helloServiceFeign.sayHi("liu");
    }

    @GetMapping(value = "/test/go")
    public String goBack() {
        return helloServiceFeign.goBack();
    }
}
