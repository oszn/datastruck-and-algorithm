package com.example.demo.component.rpc.idservice.tools;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by a8_1 on 2018/8/3.
 */
public class IdGenerator {
    public static int toHash(String key) {
        int arraySize = 11113; // 数组大小一般取质数
        int hashCode = 0;
        for (int i = 0; i < key.length(); i++) { // 从字符串的左边开始计算
            int letterValue = key.charAt(i) - 96;// 将获取到的字符串转换成数字，比如a的码值是97，则97-96=1
            // 就代表a的值，同理b=2；
            hashCode = ((hashCode << 5) + letterValue) % arraySize;// 防止编码溢出，对每步结果都进行取模运算
        }
        return hashCode;
    }

    private static final AtomicInteger SERIAL = new AtomicInteger(0);

    /**
     * timestamp to 2100-01-01 is 4102416000000, < (1 << 42)
     * Rule:
     * |<-- 45 bit -->|<-- 6 bit -->|<-- 12 bit -->|
     * |  time second |  server id   | atomic byte |
     *
     * @param type
     * @return unique id
     */
    public static Long generate(int type) {
        Long now = System.currentTimeMillis();
        int typeId = type;
        int num = SERIAL.incrementAndGet();
        if (num >= (1 << 12)) {
            SERIAL.set(0);
        }
        Long id = (now << 18)
                + (typeId << 12 )
                + num;
        return id;
    }

    public static String generateOrderId(int type){
        return type + generate(type).toString();
    }

    public static String genId() {
        Random rand = new Random();
        int randNum = rand.nextInt(100);
        if (randNum < 10) {
            randNum += 10;
        }

        String preStr = String.format("%02d", randNum);
        String postStr = String.format("%d", rand.nextInt(10));
        String middleNum = String.valueOf(System.currentTimeMillis()).substring(8, 13);
        StringBuilder pid = new StringBuilder();
        return pid.append(preStr).append(middleNum).append(postStr).toString();
    }
}
