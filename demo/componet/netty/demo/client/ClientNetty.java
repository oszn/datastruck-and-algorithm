package demo.client;

import demo.handler.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class ClientNetty {
    public static void main(String[] args) throws Exception {
        //1、创建一个线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //2、创建客户端启动助手，完成相关配置
        Bootstrap b = new Bootstrap();
        b.group(group)//3、设置线程组
                .channel(NioSocketChannel.class)//4、设置客户端通道的实现类
                .handler(new ChannelInitializer<SocketChannel>() {//创建一个初始化通道对象
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new ServerMsgEncode());//对 String 对象自动编码,属于出站站处理器
                        sc.pipeline().addLast(new ServerMsgDecode());//把网络字节流自动解码为 String 对象，属于入站处理器
                        //6、在pipline中添加自定义的handler
                        sc.pipeline().addLast(new NettyClientHandler());
                    }
                });
        System.out.println("【客户端已启动】");
        //7、启动客户端去连接服务器端 connect方法是异步的，sync方法是同步阻塞的
        ChannelFuture cf = b.connect("127.0.0.1", 9999).sync();
        System.out.println("---" + cf.channel().remoteAddress() + "------");
        //循环监听用户键盘输入
        String name = "wang";
        String to="liu";
        LoginData loginData = new LoginData();
        loginData.setName(name);
        cf.channel().writeAndFlush(loginData);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            System.out.println("用户输入：" + msg);
            MsgData msgData = new MsgData();
            msgData.setMsg(msg);

            //通过channel发送到服务器端
            SingleMsg singleMsg = new SingleMsg();
            singleMsg.setMsgData(msgData);
            singleMsg.setFrom(name);
            singleMsg.setTo(to);
            singleMsg.setType(ToType.SINGLE);
            cf.channel().writeAndFlush(singleMsg);
        }
        //8、关闭连接（异步非阻塞）
        cf.channel().closeFuture().sync();
        System.out.print("Client is end.....");
    }
}