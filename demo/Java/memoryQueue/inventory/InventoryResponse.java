package com.example.demo.component.concurrent.inventory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryResponse {
    private Integer id;
    private Integer idx;
    private ReentrantLock lock;
    private Condition condition;
    private String result;
    private boolean isSuccess;

    public InventoryResponse() {
        lock = new ReentrantLock();
        isSuccess = false;
        condition = lock.newCondition();
    }

    public String getResponse() {
        lock.lock();
        try {
            while (!isSuccess) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void setResponse(String result){
        if(isSuccess){
            return;
        }
        lock.lock();
        try {
            this.result=result;
            isSuccess=true;
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "InventoryResponse{" +
                "id=" + id +
                ", idx=" + idx +
                ", result='" + result + '\'' +
                '}';
    }
}
