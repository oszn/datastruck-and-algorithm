package com.example.demo.component.rpc.client;

import com.example.demo.component.cache.RedisCache;
import com.example.demo.component.rpc.client.constant.NettyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(NettyConstant.ClientName)
public class NettyBean {
    @Autowired
    RedisCache redisCache;
    ConcurrentHashMap<Integer, NettyClient> nettyMap = new ConcurrentHashMap<>();

    public NettyClient search(int port) {
        NettyClient nettyClient=nettyMap.get(port);
//        if(nettyClient==null){
//            nettyClient=new NettyClient();
//            nettyClient.connect(port);
//            nettyMap.put(port,nettyClient);
//        }
        return nettyClient;
    }

    public void insert(int key, NettyClient value) {
        nettyMap.put(key, value);
    }
}
