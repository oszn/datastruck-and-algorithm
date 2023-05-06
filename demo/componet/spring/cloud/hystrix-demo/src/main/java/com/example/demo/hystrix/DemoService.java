package com.example.demo.hystrix;

import com.netflix.hystrix.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class DemoService extends HystrixCommand<String> {
    private static final HystrixCommand.Setter cacheSetter = HystrixCommand.Setter.
            withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).
            andCommandKey(HystrixCommandKey.Factory.asKey("Hello")).
            andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(3000));
    String name;

    public DemoService(String name) {
        super(cacheSetter);
        this.name = name;
    }

    public String sayHi(String name) {
        return "hi" + name;
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(2000);
        return "hi-" + name;
    }

    @Override
    protected String getFallback() {
        return "error-" + name;
    }

    public static void main(String[] args) {
        List<Future> futures=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            HystrixCommand<String> demo = new DemoService(String.valueOf(i));
            HystrixCommand<String> demo2=new GroupTestService(String.valueOf(i));
            HystrixCommand<String> demo3=new ThreadPoolPro(String.valueOf(i));
            Future<String> result = demo.queue();
            futures.add(demo2.queue());
            futures.add(demo3.queue());
            futures.add(result);

        }
        for(int i=0;i<futures.size();i++){
            try {
                System.out.println(futures.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
