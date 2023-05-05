package com.example.demo.component.rpc.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.InstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer{
    public static ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.setReferences(true);//检测循环依赖，默认值为true，避免版本变化显示设置
        //方式一：设置无需注册，那么之后就无需对需要进行序列号的类进行注册（性能略差）
        //kryo.setRegistrationRequired(false);//默认值为true，避免版本变化显示设置
        ((Kryo.DefaultInstantiatorStrategy)kryo.getInstantiatorStrategy())
                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());//设置默认的实例化器
        //方式二：由于默认是需要进行注册的，若是不设置为false，那么就需要进行手动注册class类

        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final Output output = new Output(baos)
        ) {
            Kryo kryo = kryoThreadLocal.get();
            //进行序列化
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed");
        }
    }
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(obj);
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed");
        }
    }
}
