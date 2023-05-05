package demo.server;

import demo.handler.MsgData;
import demo.handler.SingleMsg;
import demo.handler.ToType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MsgFilterHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        System.out.println("【客户端】"+remoteAddress+":"+msg);
        msg=(SingleMsg)msg;
        if(((SingleMsg) msg).getType()== ToType.SINGLE){

        }
    }
}
