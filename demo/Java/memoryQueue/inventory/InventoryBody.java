package com.example.demo.component.concurrent.inventory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;

public class InventoryBody {
    private Integer id;
    private Integer idx;
    private CountDownLatch condition;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    @Override
    public String toString() {
        return "InventoryBody{" +
                "id=" + id +
                ", idx=" + idx +
                '}';
    }

    public CountDownLatch getCondition() {
        return condition;
    }

    public void setCondition(CountDownLatch condition) {
        this.condition = condition;
    }
}
