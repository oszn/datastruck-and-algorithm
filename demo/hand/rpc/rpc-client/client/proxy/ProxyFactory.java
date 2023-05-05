package com.example.demo.component.rpc.client.proxy;


import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Proxy;


@Slf4j
public class ProxyFactory<S> {
    public static <T> T create(Class<T> interfaceClass) {
//        long now=System.currentTimeMillis();
        T ret = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new RequestDynamicProxy<T>(interfaceClass));
//        log.info(String.valueOf(System.currentTimeMillis()-now));
        return ret;
    }
}
