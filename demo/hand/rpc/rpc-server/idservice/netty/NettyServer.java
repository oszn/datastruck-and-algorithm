package com.example.demo.component.rpc.idservice.netty;

import com.example.demo.component.cache.RedisCache;
import com.example.demo.component.config.DeviceIdConf;
import com.example.demo.component.rpc.common.RpcRequest;
import com.example.demo.component.rpc.common.RpcResponse;
import com.example.demo.component.rpc.common.ServerMsgDecode;
//import com.example.demo.component.rpc.common.ServerMsgEncode;
import com.example.demo.component.rpc.common.ServerMsgEncode;
import com.example.demo.component.rpc.idservice.handler.ServerHandler;
//import com.example.demo.rpc.common.ServerMsgDecode;
//import com.example.demo.rpc.common.ServerMsgEncode;
import com.example.demo.component.rpc.register.zk.ZkApi;
import com.example.demo.component.util.BeanFactoryUtil;
import com.example.demo.pubdef.constance.BeanNameConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Random;

@Slf4j
@Component
public class NettyServer implements InitializingBean {
    private EventLoopGroup boss = null;
    private EventLoopGroup worker = null;
    @Autowired
    private ServerHandler serverHandler;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DeviceIdConf deviceIdConf;
    @Autowired
    private ZkApi zkApi;

    @Override
    public void afterPropertiesSet() throws Exception {
//        //zookeeper is used here as the registry, which is not covered in this article and can be ignored.
//        ServiceRegistry registry = new ZkServiceRegistry("127.0.0.1:2181");
//        start(registry);
//        System.out.println("ass");
        start();
    }

    public void start() throws Exception {
        //Thread pool responsible for handling client connections
        boss = new NioEventLoopGroup();
        //Thread pool for read and write operations
        worker = new NioEventLoopGroup();
        String ip = "127.0.0.1";
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //Add Decoder
                        pipeline.addLast(new ServerMsgDecode(RpcRequest.class));
                        //Add encoder
                        pipeline.addLast(new ServerMsgEncode(RpcResponse.class));
                        pipeline.addLast(serverHandler);
                        //Adding Request Processor


                    }
                });
        Random random = new Random();
        int port = random.nextInt(1000) + 10000;
//        log.info("from{device}",);
        String key = BeanNameConstant.DeviceId + deviceIdConf.deviceId;
        redisCache.set(key, String.valueOf(port));
        redisCache.lpush("Rpc-PropHandler", key);
        redisCache.lpush("Rpc-IdGeneratorService", key);
        bind(serverBootstrap, port);
//        System.out.println("success");
        if (zkApi.exists("/PropHandler",false)==null) {
            zkApi.createNode("/PropHandler", ip + ":" + port);
        }
        zkApi.createNodeTemp("/PropHandler/"+deviceIdConf.deviceId,ip + ":" + port);
    }

    /**
     * If port binding fails, port number + 1, rebind
     *
     * @param serverBootstrap
     * @param port
     */
    public void bind(final ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
//                log.info("port[ {} ] Binding success",port);
            } else {
//                log.error("port[ {} ] Binding failed", port);
                bind(serverBootstrap, port + 1);
            }
        });
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();
//        log.info("Close Netty");
    }
}