package com.example.demo.component.concurrent.inventory;

import com.example.demo.component.util.BeanFactoryUtil;
import com.example.demo.dao.entry.commodity.InventoryListTest;
import com.example.demo.dao.mapper.commodity.InventoryListTestMapper;
import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static com.example.demo.component.concurrent.inventory.PusherConsumer.*;

@Slf4j
@Service
class MyTask extends TimerTask {
    int idx;
    @Autowired
    InventoryListTestMapper mapper;

    InventoryListTestMapper innerMap;

    public MyTask(int idx) {
        this.idx = idx;
        innerMap = (InventoryListTestMapper) BeanFactoryUtil.getBean("inventoryListTestMapper");
//        mapper = (InventoryListTestMapper) BeanFactoryUtil.getBean("invx");
    }

    public MyTask() {
        this.idx = idx;
    }

    @Override
    public void run() {
        int partition = getK(idx, k);
        ConcurrentLinkedQueue queue = maps.get(partition);
        if (queue.isEmpty()) {
            return;
        }
        log.info("queue->{}", queue.toString());
//            System.out.println("start");
        ConcurrentHashMap<Integer, BatchInventoryResponse> gather = new ConcurrentHashMap<>();
        int k = 500;
        try {
            List<InventoryResponse> res = new ArrayList<>();
//            Queues.drain(queue, res, 100, 1, TimeUnit.MINUTES);
            while (k > 0 && !queue.isEmpty()) {
                InventoryResponse body = (InventoryResponse) queue.poll();
                if (!gather.containsKey(body.getId())) {
                    gather.put(body.getId(), new BatchInventoryResponse());
                }
//                    log.info("thread{},idx{}, consumer body{}", Thread.currentThread().getName(), idx, body);
                gather.get(body.getId()).add(body);
                k--;
            }
            log.info("consume:->{}", k);
            for (Map.Entry<Integer, BatchInventoryResponse> ga : gather.entrySet()) {
                BatchInventoryResponse list = ga.getValue();
//                log.info("[inner list]->{}", list);
                List<InventoryListTest> tests = list.convert();
//                log.info("[test]->{}", tests);
                if (tests.size() != 0) {
                    innerMap.insertList(tests);
                }
                //                    AtomicInteger num =new AtomicInteger();
                for (int i = 0; i < list.size(); i++) {
//                        num.getAndIncrement();
                    list.get(i).setResponse("success");
                    out.getAndIncrement();
//                        cnt.get(list.get(i)).decrementAndGet();
                }
//                cnt.get(ga.getKey()).getAndAdd(-list.size());

            }
//            log.info("in qps->{},out qps->{}", in, out);
//            log.info("[partition:{}] cnt->{}", partition, cnt.toString());
//                System.out.println("end");
        } catch (Exception e) {
            throw e;
        } finally {

        }
    }
}