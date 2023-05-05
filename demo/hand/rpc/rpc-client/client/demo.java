package com.example.demo.component.rpc.client;

import com.example.demo.component.rpc.client.proxy.ProxyFactory;
import com.example.demo.remote.IdGeneratorService;
import com.example.demo.remote.PropHandler;

import java.math.BigDecimal;

public class demo {
    public static void main(String[] args) {
//        EmailService emailService;
//        IdGeneratorService service = ProxyFactory.create(IdGeneratorService.class);
//        System.out.println(service.createId("liu"));
        PropHandler propHandler=ProxyFactory.create(PropHandler.class);
        System.out.println(propHandler.useProp(1L,1,1,new BigDecimal("111")));
    }
}
