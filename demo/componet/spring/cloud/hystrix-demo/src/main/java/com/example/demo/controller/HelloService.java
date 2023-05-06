package com.example.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloService {
    @HystrixCommand(fallbackMethod = "fallback_zs1",commandProperties = {
            //设置这个线程的超时时间是3s，3s内是正常的业务逻辑，超过3s调用fallbackMethod指定的方法进行处理
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")
    })
    @GetMapping(value = "/hello/say")
    public String sayHi(@RequestParam("name") String name) throws InterruptedException {
        Thread.sleep(4000);
        System.out.println(name);
        return "hello" + name;
    }
    public String fallback_zs1(String name ){
        String fallBack="ConsumerHystrix-"+"fallback_zs1-";
        return fallBack+name;
    }

    //服务熔断
    @HystrixCommand(fallbackMethod = "fallback_zs2",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),   //是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "1"),  //请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),    //时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "5"),    //失败率达到多少后跳闸
    })
    @GetMapping("/zs2")
    public String zs2(String name){
        String  test=  "ConsumerHystrix-"+"zs2-";
        return test+ name;
    }

    public String fallback_zs2(String name ){
        String fallBack="ConsumerHystrix-"+"fallback_zs2-";
        return fallBack+name;
    }
}
