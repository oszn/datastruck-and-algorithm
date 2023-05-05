package com.example.demo.component.rpc.idservice.handler;


import com.example.demo.component.exception.BusinessException;
import com.example.demo.netty.RpcRequest;
import com.example.demo.netty.RpcResponse;
import com.example.demo.pubdef.dto.BaseRsp;
import com.example.demo.pubdef.dto.ResultEnum;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

//import java.lang.reflect.InvocationTargetException;

@Slf4j
@ChannelHandler.Sharable
@Component
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
//        System.out.println("?");
        String uuid=request.getUuid();
        RpcResponse response=new RpcResponse();
        response.setUuid(uuid);
        BaseRsp ret=null;
        try {
            Object result=handler(request);
            ret=new BaseRsp(ResultEnum.SUCCESS,result);
        }catch (BusinessException e){
            log.info("{}",e.getResultEnum());
            ret=new BaseRsp(e.getResultEnum(),null);
        }catch (Exception e){
            ret=new BaseRsp(ResultEnum.TEST_PAY_FAIL);
        }

//        response.setRet(result)
        response.setRet(ret);
        channelHandlerContext.writeAndFlush(response);
    }

    //反射过程
    public Object handler(RpcRequest request){
        String className=request.getClassName();
        try {
            long s3=System.currentTimeMillis();
            String[]cla=className.split("\\.");
            className="com.example.demo.remote."+cla[cla.length-1];
            Class<?> c=Class.forName(className);
            String method=request.getMethodName();
            Object ServiceBean=applicationContext.getBean(c);
            Class<?> serviceClass = ServiceBean.getClass();
            Object[] parameters=request.getParameters();
            Class<?>[] types= request.getParameterTypes();
            FastClass fastClass=FastClass.create(serviceClass);
            FastMethod fastMethod=fastClass.getMethod(method,types);

            long s1=System.currentTimeMillis();
            Object obj= fastMethod.invoke(ServiceBean,parameters);
            long s2=System.currentTimeMillis();
            log.info("invoke time -> {},before time -> {}",s2-s1,s3-s1);
            return obj;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            if(e.getTargetException() instanceof BusinessException){
                throw new BusinessException(((BusinessException) e.getTargetException()).getResultEnum());
            }
            e.printStackTrace();
        }catch (BusinessException e){
            throw new BusinessException(e.getResultEnum());
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}