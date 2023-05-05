package demo.handler;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;

public class MsgDataEncoder<T> extends MessageToByteEncoder<T> {
    private final Charset charset = Charset.forName("UTF-8");
    public Kryo kryo = new Kryo();
    public byte[] convertToBytes(T car) {

        ByteArrayOutputStream bos = null;
        Output output = null;
        try {
            bos = new ByteArrayOutputStream();
            output = new Output(bos);
            kryo.writeClassAndObject(output, car);
            output.flush();

            return bos.toByteArray();
        } catch (KryoException e) {
            e.printStackTrace();
        }finally{
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(bos);
        }
        return null;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, T msgData, ByteBuf byteBuf) throws Exception {
        byte[]body=convertToBytes(msgData);
        int dataLen=body.length;
        byteBuf.writeInt(dataLen);
        byteBuf.writeBytes(body);
    }
}
