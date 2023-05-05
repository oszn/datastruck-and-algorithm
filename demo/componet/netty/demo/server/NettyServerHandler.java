package demo.server;

import demo.handler.MsgData;
import demo.handler.SingleMsg;
import demo.handler.ToType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    //内部会用ConcurrentMap来维护，线程安全
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Map<String, ChannelId> user_map = new HashMap<>();

    /**
     * channel处于就绪状态，客户端刚上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        String remoteAddress = channel.remoteAddress().toString();
        //加入全局变量中
        //channelGroup.writeAndFlush(Unpooled.copiedBuffer("【客户端】"+remoteAddress+"上线啦 "+format.format(new Date()),CharsetUtil.UTF_8));
        SingleMsg singleMsg=new SingleMsg();
        MsgData msgData=new MsgData();
        msgData.setMsg("【客户端】" + remoteAddress + "上线啦 " + format.format(new Date()));
        singleMsg.setType(ToType.GROUP);
        singleMsg.setMsgData(msgData);
//        singleMsg.setMsgData(new MsgData().setMsg("【客户端】" + remoteAddress + "上线啦 " + format.format(new Date())));
        channelGroup.writeAndFlush(singleMsg);
        channelGroup.add(channel);
//        user_map.put(remoteAddress, channel.id());
        //将当前channel加入到ChannelGroup
        System.out.println("【客户端】" + remoteAddress + "上线啦");
    }

    /**
     * channel离线
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        channelGroup.remove(channel);
        channelGroup.writeAndFlush("【客户端】" + remoteAddress + "已下线 " + format.format(new Date()));
        System.out.println("【客户端】" + remoteAddress + "已下线");
    }

    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        System.out.println("【客户端】" + remoteAddress + ":" + msg);
        msg = (SingleMsg) msg;
        Object finalMsg = msg;
        String name=((SingleMsg) msg).getFrom();
        String to=((SingleMsg) msg).getTo();
        if (((SingleMsg) msg).getType() == ToType.SINGLE) {

            Channel c = channelGroup.find(LoginHandler.user_map.get(((SingleMsg) msg).getTo()));
            c.writeAndFlush(msg);
        } else if (((SingleMsg) msg).getType() == ToType.GROUP) {
            channelGroup.forEach(ch -> {
                        if (ch == channel) {
                            ((SingleMsg) finalMsg).getMsgData().setName("self");
//                        ((MsgData) msg).setName("self");
                            ch.writeAndFlush(finalMsg);
                        } else {
                            ch.writeAndFlush("【客户端】" + remoteAddress + ":" + finalMsg);
                        }
                    }
            );
        }else{
            channel.writeAndFlush(msg);
        }
    }
}