package demo.server;

import demo.handler.LoginData;
import demo.handler.SingleMsg;
import demo.handler.ToType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Signal;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.SystemPropertyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginHandler extends ChannelInboundHandlerAdapter {
    public static Map<String, ChannelId> user_map = new HashMap<>();
    private static Map<String, List<SingleMsg>> left_msg = new HashMap<>();

    //    private Map<ChannelId,>

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (msg.getClass() == LoginData.class) {
            String name = ((LoginData) msg).getName();
            if (left_msg.containsKey(name)) {
                for (SingleMsg lmsg : left_msg.get(name)) {
                    channel.writeAndFlush(lmsg);
                }
                left_msg.remove(name);
            }

            user_map.put(name, channel.id());
        }
        System.out.println(user_map.size());
        if (msg.getClass() == SingleMsg.class) {
            if (((SingleMsg) msg).getType() == ToType.SINGLE) {
                String to = ((SingleMsg) msg).getTo();
                System.out.println(to);
                if (!user_map.containsKey(to) && !to.equals(((SingleMsg) msg).getFrom())) {
                    if (!left_msg.containsKey(to)) {
                        left_msg.put(to, new ArrayList<>());
//                        left_msg.
                    }
                    List<SingleMsg> list = left_msg.get(to);
                    SingleMsg mm= (SingleMsg) msg;
                    list.add(mm.deepCopy());
                    ((SingleMsg) msg).getMsgData().setMsg("the mrs" + ((SingleMsg) msg).getTo() + "not in the line");
                    ctx.writeAndFlush(msg);
                    return;
                }
            }
            ctx.fireChannelRead(msg);
        }
    }

}
