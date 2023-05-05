package invoke.dynamic;

import invoke.demo.Human;
import invoke.demo.Person;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements InvocationHandler {
    public static void main(String[] args) {
//        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
//        DynamicProxy dynamicProxy = new DynamicProxy();
//        Human p = new Human();
//        dynamicProxy.setObj(p);
//        Person x = (Person) Proxy.newProxyInstance(dynamicProxy.getClass().getClassLoader(), new Class[]{Person.class}, dynamicProxy);
//        x.wakeup();
        DynamicProxy dynamicProxy=new DynamicProxy();
        Person p=new Human();
        dynamicProxy.setObj(p);
        Person pe= (Person) dynamicProxy.getProxy();
        pe.wakeup();
    }

    private Object obj;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(obj, args);
    }
    public Object getProxy(){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),obj.getClass().getInterfaces(),this);
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
