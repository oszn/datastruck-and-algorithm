package com.example.demo.component.rpc.registrar;

import com.example.demo.component.rpc.client.proxy.RequestDynamicProxy;

//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Proxy;
//import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import org.springframework.beans.factory.FactoryBean;

public class ServiceFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public ServiceFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() throws Exception {
        RequestDynamicProxy handler=new RequestDynamicProxy<T>(interfaceType);
//        RequestDynamicProxy requestDynamicProxy=new RequestDynamicProxy(interfaceType);
//        T ret= (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
//                new Class[] {interfaceType}, handler);
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(interfaceType);
        enhancer.setCallback(handler);
        T ret=(T)enhancer.create();
        return ret;
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
