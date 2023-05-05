package demo.handler;

import java.io.*;

public class SingleMsg{
    MsgData msgData;
    String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public ToType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SingleMsg{" +
                "msgData=" + msgData +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", type=" + type +
                '}';
    }

    public void setType(ToType type) {
        this.type = type;
    }

    public MsgData getMsgData() {
        return msgData;
    }

    public void setMsgData(MsgData msgData) {
        this.msgData = msgData;
    }

    String to;
    ToType type;

    public SingleMsg deepCopy() throws Exception
    {
        // 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。
        // 所以利用这个特性可以实现对象的深拷贝。
        SingleMsg msg=new SingleMsg();
        MsgData msgData=new MsgData(this.msgData);
        msg.setType(this.type);
        msg.setMsgData(msgData);
        msg.setFrom(from);
        msg.setTo(to);
        return msg;
    }
}
