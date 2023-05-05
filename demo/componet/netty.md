# netty

写了一个小的聊天demo，烂大街的那种项目，但作为demo学习netty整体的过程，足够。

## 简介

聊天系统，抽象点就是点对点系统。定义一个消息发送的结构体，就完成核心的工作。

```java
public class SingleMsg{
    MsgData msgData;
    String from;
    String to;
    ToType type;
    ...
}
public class MsgData {
    String name;
    String msg;
    String data;
    int device;
    ...
}
public enum ToType {
    SELF,GROUP,SINGLE,EMPTY;
}
```

功能性需求。

1. 发送消息
2. 接受消息
3. 群发、发给自己、单发。



## 解决方案

用户全是客户端，服务端做一个消息记录转发的过程（甚至不需要记录）。

核心过程。

1. 序列化反序列化。
2. 包的切分。
3. 服务器到用户的通信。

这些netty刚好都能做，或者很好做，所以很容易实现。



## 学习过程种总结的文章

[Java的NIO](https://blog.csdn.net/weixin_42771401/article/details/128733754?spm=1001.2014.3001.5501)

[netty理论部分](https://blog.csdn.net/weixin_42771401/article/details/128741148?spm=1001.2014.3001.5501)

[netty实战（一）聊天服务](https://blog.csdn.net/weixin_42771401/article/details/128746758?spm=1001.2014.3001.5501)

[netty实战（二）聊天服务](https://blog.csdn.net/weixin_42771401/article/details/128750352?spm=1001.2014.3001.5501)