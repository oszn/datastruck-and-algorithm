package com.example.demo.component.rpc.client;

import com.example.demo.component.rpc.client.handler.HoldBackHandler;
import com.example.demo.component.rpc.common.RpcRequest;
import com.example.demo.component.rpc.common.RpcResponse;
import com.example.demo.component.rpc.common.ServerMsgDecode;
import com.example.demo.component.rpc.common.ServerMsgEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public class NettyClient {
    private Bootstrap b;
    private ChannelFuture f;
    private HoldBackHandler backHandler;
    EventLoopGroup workerGroup;
    public void connect(int port) {
        String host = "localhost";
//        int port = 8888;
        workerGroup= new NioEventLoopGroup();
        backHandler=new HoldBackHandler();
        try {
            b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,2000);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new ServerMsgEncode(RpcRequest.class),
                            new ServerMsgDecode(RpcResponse.class), backHandler);
                }
            });
            f = b.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void reConnect(int port){
        String host = "localhost";
        try {
            f=b.connect(host,port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public RpcResponse send(RpcRequest request){
        try {
            f.channel().writeAndFlush(request).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RpcResponse response=backHandler.getRspResponse(request.getUuid());
//        System.out.println(response.getUuid());
        return response;
    }

    public void close() {
        workerGroup.shutdownGracefully();
        f.channel().closeFuture().syncUninterruptibly();
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        connect();
//    }
}
