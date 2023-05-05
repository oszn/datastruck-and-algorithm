package com.example.demo.component.rpc.client.handler;

import com.example.demo.component.rpc.common.RpcRequest;
import com.example.demo.component.rpc.common.RpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;


import java.util.HashMap;
import java.util.Map;

public class HoldBackHandler extends ChannelDuplexHandler {
    private Map<String,DefaultFuture> futureMap= new HashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            //获取响应对象
            RpcResponse response = (RpcResponse) msg;
            DefaultFuture defaultFuture = futureMap.get(response.getUuid());
            defaultFuture.setResponse(response);
        }
        super.channelRead(ctx,msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) msg;
            //发送请求对象之前，先把请求ID保存下来，并构建一个与响应Future的映射关系
            futureMap.putIfAbsent(request.getUuid(), new DefaultFuture());
        }
        super.write(ctx, msg, promise);
    }
    public RpcResponse getRspResponse(String uuid){
        try {
            DefaultFuture f=futureMap.get(uuid);
            return f.getResponse(1000);
        }finally {
            futureMap.remove(uuid);
        }

    }
}
