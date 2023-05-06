package com.example.demo.hystrix;

import com.netflix.hystrix.*;

public class ThreadPoolPro extends HystrixCommand<String> {
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

    String name;

    @Override
    protected String run() throws Exception {
        Thread.sleep(2000);
        return "threadPool-" + name;
    }
    @Override
    protected String getFallback() {
        return "thread-error-" + name;
    }
}
