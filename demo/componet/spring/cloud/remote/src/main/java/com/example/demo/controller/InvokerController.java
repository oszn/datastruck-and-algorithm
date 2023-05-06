package com.example.demo.controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Configuration
public class InvokerController {
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @RequestMapping(value = "/router", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String router(){
        RestTemplate restTpl = getRestTemplate();
        //根据应用名称调用服务
        String json = restTpl.getForObject("http://eureka-client/test/say", String.class);
        return json;
    }
}
