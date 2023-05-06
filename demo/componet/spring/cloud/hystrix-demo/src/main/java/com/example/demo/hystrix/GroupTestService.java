package com.example.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class GroupTestService extends HystrixCommand<String> {
    private static final HystrixCommand.Setter cacheSetter = HystrixCommand.Setter.
            withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).
            andCommandKey(HystrixCommandKey.Factory.asKey("Group")).
            andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(3000));

    public GroupTestService(String name) {
        super(cacheSetter);
        this.name = name;
    }

    String name;

    @Override
    protected String run() throws Exception {
        Thread.sleep(2000);
        return "group-" + name;
    }
    @Override
    protected String getFallback() {
        return "group-error-" + name;
    }
}
