package com.example.demo.component.rpc.client.handler;


import com.example.demo.component.rpc.common.RpcResponse;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
    private RpcResponse response;
    private Boolean isSuccess;
    ReentrantLock lock;
    Condition ready;
    public DefaultFuture() {
        lock = new ReentrantLock();
        isSuccess = Boolean.FALSE;
        ready=lock.newCondition();
    }

    public RpcResponse getResponse(long millions) {
        lock.lock();
        try {
            while (!isSuccess) {
                try {
                    ready.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            lock.unlock();
        }
        return response;
    }

    public void setResponse(RpcResponse response){
        if(isSuccess){
            return;
        }
        lock.lock();
        try {
            this.response=response;
            isSuccess=true;
            ready.signal();
        }finally {
            lock.unlock();
        }
    }
}
