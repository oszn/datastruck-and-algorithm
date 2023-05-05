package demo.handler;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;

public class SingleMsgEncode extends MsgDataEncoder<SingleMsg>{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SingleMsg msgData, ByteBuf byteBuf) throws Exception {
        byte[]body=convertToBytes(msgData);
        int dataLen=body.length;
        byteBuf.writeInt(dataLen);
        byteBuf.writeBytes(body);
    }

}
