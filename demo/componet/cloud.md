# 微服务

感受：springcloud非常强大，让微服务来说，变得非常的简单起来。

[toc]

整体代码在/spring/cloud下面。cloud放的大部分是文档和实验代码。

## eureka

>[good link](https://chanxinguidao.github.io/springcloud/2019/05/20/SpringCloud%E6%95%99%E7%A8%8B%E7%AC%AC1%E7%AF%87-Eureka.html)
>
>[官方文档](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
>
>https://www.cnblogs.com/yy3b2007com/p/11556891.html
>
>https://cloud.tencent.com/developer/beta/article/1449945

### 作用

服务的发现与注册中心。类似于中介性质的。

![img](https://img2018.cnblogs.com/blog/201408/201911/201408-20191102230458401-781280397.png)

**角色**

我们需要细分eureka中的角色。

1. 注册中心（Eureka Server）
2. 服务的提供者（Eureka Client）
3. 服务的调用者（Eureka Client）
4. 注册中心副本（Eureka Server）

其中4是非必要的。

**抽象过程**

其实上面的4个角色不限于eureka，放在任何一个注册中心都是成立的。

角色分为2种server与client。

client提供服务与请求服务。由于一个服务可能有多个服务提供者，他们提供方式可能相同，也可能不同。但都会有一个共同的代号，而代号后面是一个list，也就是具体的请求地址。

所以一次完整注册中心过程如下。

1. 注册中心S启动（server）
2. 服务提供者A（client）启动，将服务名称C与ip端口信息注册到S，在S中表现如下{C:[A.ip+A.port]}。
3. 服务提供者B（client）启动，将服务名称C（与A提供相同的服务）与ip端口信息注册到S，现在S中表现如下{C:[A.ip+A.port，B.ip+B.port]}。
4. 服务提供者M（client）启动，将服务名称D与ip端口信息注册到S，现在S中表现如下{C:[A.ip+A.port，B.ip+B.port]，D:[M.ip+M.port]}。
5. 服务请求者N（client）启动，请求者想要调用ABM的服务，必须要首先知道2点，1.注册中心S的地址，2.服务的名称（C或者D）。
6. N请求S询问服务C，S返回信息C:[A.ip+A.port，B.ip+B.port]。
7. N请求C进行访问。

### eureka相关参数

从上述抽象过程可知。每个服务的提供者需要一个名字，将自己注册到服务器，而相互的通信都是通过这个名字。

**server**

```yml
eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

@EnableEurekaServer

**client**

```yml
spring:
  application:
    name: eureka-client
eureka:
  instance:
    instance-id: eureka-client # 修改 Eureka上的默认描述信息
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

```
@EnableEurekaClient
```

name是节点的app的节点名称，也可以使用instance-id修改默认的描述信息。

 register-with-eureka: 是否将自己注册进入。
 fetch-registry: 是否能从其中取。

### 服务提供

服务提供，很简单，没有做很多的复杂处理，就是把自己的服务名称以及对应的ip注册到注册中心S。

### 服务调用

服务调用，同理只需要访问组测中心S，以及对应的服务名称即可，必须提前先知道服务的对应名称。但是需要将请求的服务名称转化为对应ip和端口的过程。

例如

```
http://eureka-client/test/say
将eureka-client转化成对应的localhost:9000或者localhost:9001（2台服务）。
```

普通的RestTemplate可以这么写。

```java
package com.example.demo.controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Configuration
public class InvokerController {
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @RequestMapping(value = "/router", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String router(){
        RestTemplate restTpl = getRestTemplate();
        //根据应用名称调用服务
        String json = restTpl.getForObject("http://eureka-client/test/say", String.class);
        return json;
    }
}
```

### next

既然都是注册中心，那当然有限看zookeeper的内容。最近2天开始看吧。

## feign

[feign的demo总结文档](./feign.md)

feign可以很容易的将请求包装成一个http包，发送并处理。将打包与解析包的过程简化，原理是动态代理。

那么fegin想要和注册中心组合怎么办。

也就是如何替换掉相关的请求。



### feign参数

```Java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FeignClient {
    @AliasFor("name")
    String value() default "";

    String contextId() default "";

    @AliasFor("value")
    String name() default "";

    /** @deprecated */
    @Deprecated
    String qualifier() default "";

    String[] qualifiers() default {};

    String url() default "";

    boolean decode404() default false;

    Class<?>[] configuration() default {};

    Class<?> fallback() default void.class;

    Class<?> fallbackFactory() default void.class;

    String path() default "";

    boolean primary() default true;
}
```

**value**

对应的是服务的名称，对应的例如eureka中注册的服务名称。

**contextId**

一个服务中有多个相同的feignClient，而feignClient是要注册需要一个唯一id，所以是需要一个contextId进行区分的。

**name**

和value互为别名，作用类似。可以配合其他的组件使用，例如ribbon需要使用name，其他的组件可能需要value。



### issue

注册中心可以换很多种，eureka或者zookeeper，但是feign不需要做调整，只能说springcloud设计上，肯定侵入了feign的代码，所以feign压根不需要调整。埋个眼，看看cloud到底做了什么，才可以不需要关注谁是注册中心。



## hystrix

https://cn.dubbo.apache.org/zh-cn/overview/what/ecosystem/rate-limit/hystrix/

Hystrix 是什么？

>在分布式系统中，每个服务都可能会调用很多其他服务，被调用的那些服务就是**依赖服务**，有的时候某些依赖服务出现故障也是很正常的。
>
>Hystrix 可以让我们在分布式系统中对服务间的调用进行控制，加入一些**调用延迟**或者**依赖故障**的**容错机制**。
>
>Hystrix 通过将依赖服务进行**资源隔离**，进而阻止某个依赖服务出现故障时在整个系统所有的依赖服务调用中进行蔓延；同时 Hystrix 还提供故障时的 fallback 降级机制。
>
>**总而言之，Hystrix 通过这些方法帮助我们提升分布式系统的可用性和稳定性。**
>
>https://github.com/doocs/advanced-java/blob/main/docs/high-availability/hystrix-introduction.md



简单说，一个思想，限定资源。换句话说，带界面和高级拒绝策略的高级线程池。

**解决什么问题？**

正常应用中可能会有多方的服务，比如一个订单系统D的订单创建中中依赖了库存服务V和支付服务P。如果P系统出了问题时间花费成了原来的10倍，刚好V和P在同一个Spring中（不需要讲究合理性），那么P的处理会积压导致P可能会和V线程资源竞争剧烈，导致V系统跟着倒霉。为了避免上面的情况出现，给V和P需要限定资源，例如V和P所在的设备一共能创建100线程，那就每个人分40个线程资源，对资源进行隔离，不至于全都g了。

**拒绝策略？**

当然，Hystrix不只有线程一种计数方法，还有信号量。引用中对应的**调用延迟**或者**依赖故障**的**容错机制**，其实就是拒绝策略触发的方式。

>https://github.com/doocs/advanced-java/blob/main/docs/high-availability/hystrix-process.md
>
>在以下几种情况中，Hystrix 会调用 fallback 降级机制。
>
>- 断路器处于打开状态；
>- 线程池/队列/semaphore 满了；
>- command 执行超时；
>- run() 或者 construct() 抛出异常。



### 线程池相关

>https://github.com/doocs/advanced-java/blob/main/docs/high-availability/hystrix-execution-isolation.md

一般的一个指令组使用的线程数目应该是固定的。所以如果同一个消费组内，希望分配不同的资源，那就可以选择设置线程池的key。

限定资源，但是如果想绑定到不同的线程池可以的。

```Java
 private static final HystrixCommand.Setter cacheSetter = HystrixCommand.Setter.
            withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).
            andCommandKey(HystrixCommandKey.Factory.asKey("Thread")).
            andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(3000)).
            andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPool")).
            andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(8).withMaxQueueSize(30).withQueueSizeRejectionThreshold(40));
    public ThreadPoolPro(String name) {
        super(ThreadPoolPro.cacheSetter);
        this.name = name;
    }
```

### 信号量相关

```java
private static final HystrixCommand.Setter cacheSetter = HystrixCommand.Setter.
            withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).
            andCommandKey(HystrixCommandKey.Factory.asKey("Group")).
            andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(3000).withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE));

```

信号量进行管理，对比于线程资源，信号量不需要额外的线程资源，减少线程切换的时候cpu的消耗，更加的轻量级，而sentencel就是信号量的。

>
>
>线程池隔离技术，并不是说去控制类似 tomcat 这种 web 容器的线程。更加严格的意义上来说，Hystrix 的线程池隔离技术，控制的是 tomcat 线程的执行。Hystrix 线程池满后，会确保说，tomcat 的线程不会因为依赖服务的接口调用延迟或故障而被 hang 住，tomcat 其它的线程不会卡死，可以快速返回，然后支撑其它的事情。
>
>线程池隔离技术，是用 Hystrix 自己的线程去执行调用；而信号量隔离技术，是直接让 tomcat 线程去调用依赖服务。信号量隔离，只是一道关卡，信号量有多少，就允许多少个 tomcat 线程通过它，然后去执行。
>
>**适用场景**：
>
>- **线程池技术**，适合绝大多数场景，比如说我们对依赖服务的网络请求的调用和访问、需要对调用的 timeout 进行控制（捕捉 timeout 超时异常）。
>- **信号量技术**，适合说你的访问不是对外部依赖的访问，而是对内部的一些比较复杂的业务逻辑的访问，并且系统内部的代码，其实不涉及任何的网络请求，那么只要做信号量的普通限流就可以了，因为不需要去捕获 timeout 类似的问题。

### springcloud

想要在springcloud中使用非常容易，一个注解解决。

核心的思想类似于上面的，普通调用过程，注解只是简化了参数设置，调用的底层应该还是和上面相同的boring。具体代码参考同包的过程。

```Java
@HystrixCommand(fallbackMethod = "fallback_zs1",commandProperties = {
        //设置这个线程的超时时间是3s，3s内是正常的业务逻辑，超过3s调用fallbackMethod指定的方法进行处理
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")
})
@GetMapping(value = "/hello/say")
public String sayHi(@RequestParam("name") String name) throws InterruptedException {
    Thread.sleep(4000);
    System.out.println(name);
    return "hello" + name;
}
```

### 学习过程产出的笔记

[hystric部分源码解读](./cloud/hystric.md)



### 总结

看了hystric的源码，其实没看多少，就是把subsriber的过程看了，其实对这个过程理解好多了。观察者，被观察者，然后就是订阅过程。当被观察发生变化通知观察者。落到hystric也就是当执行的时候，如果上一个执行完回通知观察者，我执行完了一个内容，然后被观察者继续执行。单个Command相当于只有一个的情况特例，还是就是批次的情况。
