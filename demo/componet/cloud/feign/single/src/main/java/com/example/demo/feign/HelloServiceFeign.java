package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@FeignClient(value = "localhost:8080")
public interface HelloServiceFeign {
    @RequestMapping(value = "/hello/say",method = RequestMethod.GET)
    public String sayHi(String name);
}
