package com.example.demo.component.rpc.client.proxy;


import com.example.demo.component.cache.RedisCache;
import com.example.demo.component.mq.BusinessException;
import com.example.demo.component.rpc.client.NettyBean;
import com.example.demo.component.rpc.client.NettyClient;
import com.example.demo.component.rpc.client.constant.NettyConstant;
import com.example.demo.component.rpc.common.RpcRequest;
import com.example.demo.component.rpc.common.RpcResponse;
import com.example.demo.component.util.BeanFactoryUtil;
import com.example.demo.pubdef.constance.BeanNameConstant;
import com.example.demo.pubdef.dto.BaseRsp;
import com.example.demo.pubdef.dto.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import org.springframework.beans.BeanUtils;

//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.lang.reflect.Method;

@Slf4j
public class RequestDynamicProxy<T> implements InvocationHandler {
    private Class<T> clazz;

    public RequestDynamicProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public <T> T getProxy(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

//    public <T> T getCgLib(Class<?> clazz){
//        Enhancer enhancer=new Enhancer();
//        enhancer.setSuperclass(clazz);
//        enhancer.setCallback(new RequestDynamicProxy<>());
//    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        long s1 = System.currentTimeMillis();
        RedisCache redisCache = (RedisCache) BeanFactoryUtil.getBean(BeanNameConstant.RedisCache);
//        NettyBean nettyBean= (NettyBean) BeanFactoryUtil.getBean(NettyConstant.ClientName);
        RpcRequest request = new RpcRequest();

        Class<?>[] parameterTypes = method.getParameterTypes();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String uuid = UUID.randomUUID().toString();
        request.setUuid(uuid);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(objects);
        request.setParameterTypes(parameterTypes);
        String[] classList = className.split("\\.");

        List<String> list = redisCache.lrange("Rpc-" + classList[classList.length - 1]);
//        log.info("remote list -> {}", list);
        String time = "";
        long s2 = System.currentTimeMillis();
        while (list.size() != 0) {
            int idx = (int) (Math.random() * 100) % list.size();
            String k = redisCache.get(list.get(idx));
//            log.info(k);
//            list.remove(idx);
            if (k != null) {
                time = k;
                break;
            }
            redisCache.lrem("Rpc-" + classList[classList.length - 1], list.get(idx));
            list.remove(idx);
            System.out.println(time);
        }
        if (list.size() == 0) {
            throw new BusinessException(ResultEnum.PAY_SERVER_BROKE);
        }
        int port = Integer.parseInt(time);
//        System.out.println(idx);
        long s3 = System.currentTimeMillis();
        NettyClient client = new NettyClient();
        client.connect(port);
//        NettyClient client = nettyBean.search(port);
//        boolean ready=true;
//        if(client==null){
//            ready=false;
//            client=new NettyClient();
//            client.connect(port);
//        }
//        log.info(request.getUuid());

        RpcResponse response = client.send(request);
        long s4 = System.currentTimeMillis();
        client.close();
        Object ret = response.getRet();
//        (BaseRsp)ret= (BaseRsp) ret;
//        log.info("dynamic return ->{}", response);
        BaseRsp baseRsp = new BaseRsp();
        BeanUtils.copyProperties(ret, baseRsp);
        if (baseRsp.getStatus() != ResultEnum.SUCCESS.getState()) {
            throw new BusinessException(ResultEnum.stateOf(baseRsp.getStatus()));
        }
//        log.info("dynamic return copy 1->{}",baseRsp);
//        System.out.println(baseRsp.getMessage());
        Object data = baseRsp.getData();
        Class<?> re = method.getReturnType();
        String reName = re.getName();
        String[] prefix = reName.split("\\.");
        String now = prefix[0] + "." + prefix[1] + "." + prefix[2];
        long s5 = System.currentTimeMillis();
//        if(!ready){
//            nettyBean.insert(port,client);
//        }
        if ("com.example.demo".equals(now)) {
            Object t = re.newInstance();
            BeanUtils.copyProperties(data, t);
//            log.info("dynamic return copy2->{}", t);

            long s6 = System.currentTimeMillis();
//            log.info("{},{},{},{},{},{}", s2 - s1, s3 - s2, s4 - s3, s5 - s4, s6 - s5,s6-s1);
//            System.out.println(t);
            return t;
        } else {
            long s6 = System.currentTimeMillis();
//            log.info("{},{},{},{},{},{}", s2 - s1, s3 - s2, s4 - s3, s5 - s4, s6 - s5,s6-s1);
            return data;
        }
    }
}
