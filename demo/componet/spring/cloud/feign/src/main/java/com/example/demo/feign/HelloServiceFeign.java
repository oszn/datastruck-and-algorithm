package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "first-service-invoker")
public interface HelloServiceFeign {
    @RequestMapping(value = "/hello/say", method = RequestMethod.GET)
    public String sayHi(@RequestParam("name") String name);

    @RequestMapping(value = "/hello/go", method = RequestMethod.GET)
    public String goBack();
}
