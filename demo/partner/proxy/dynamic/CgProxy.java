package invoke.dynamic;

import invoke.demo.People;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CgProxy implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object o1 = methodProxy.invokeSuper(o, objects);//关键代码:
        return o1;
    }

    public static void main(String[] args) {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(People.class);
        enhancer.setCallback(new CgProxy());
        People h = (People) enhancer.create();
        h.hi();
    }
}
