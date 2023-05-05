package com.example.demo.component.rpc.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class MsgDataEncoder<T> extends MessageToByteEncoder<T> {
    private final Charset charset = Charset.forName("UTF-8");
    public Kryo kryo = new Kryo();
    private Serializer kryoSerializer=new KryoSerializer();
    private Class<?> clazz;

    public MsgDataEncoder(Class<?> clazz){
        this.clazz=clazz;
    }
    public byte[] convertToBytes(T car) {


        try {
            return kryoSerializer.serialize(car);
        } catch (KryoException e) {
            e.printStackTrace();
        }finally{
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
