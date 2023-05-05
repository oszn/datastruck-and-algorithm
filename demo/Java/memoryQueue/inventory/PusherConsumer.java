package com.example.demo.component.concurrent.inventory;

import com.example.demo.dao.entry.commodity.InventoryListTest;
import com.example.demo.dao.mapper.commodity.InventoryListTestMapper;
import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.Executors.newFixedThreadPool;

//https://blog.csdn.net/weixin_42962634/article/details/122092386
@Slf4j
@Component
public class PusherConsumer {
    @Autowired
    InventoryListTestMapper mapper;

    static int k = 10;
    static Map<Integer, ConcurrentLinkedQueue> maps;
    public static ExecutorService executorService = newFixedThreadPool(10);
    static Lock lock = new ReentrantLock();
    static ConcurrentHashMap<Integer, AtomicInteger> cnt = new ConcurrentHashMap<>();
    static AtomicInteger in = new AtomicInteger(0);
    static AtomicInteger out = new AtomicInteger(0);
    static AtomicInteger increaseIdx = new AtomicInteger(0);

    static int getK(int idx, int mod) {
        return idx % mod;
    }

    public PusherConsumer() {
        maps = new ConcurrentHashMap<>();
        for (int i = 0; i < k; i++) {
            maps.put(i, Queues.newConcurrentLinkedQueue());
        }
    }

//    static class MyTask extends TimerTask {
//        int idx;
//
//        public MyTask(int idx) {
//            this.idx = idx;
//        }
//
//        @Override
//        public void run() {
//            int partition = getK(idx, k);
//            ConcurrentLinkedQueue queue = maps.get(partition);
//            log.info("queue->{}", queue.toString());
////            System.out.println("start");
//            ConcurrentHashMap<Integer, BatchInventoryResponse> gather = new ConcurrentHashMap<>();
//            int k = 200;
//            try {
//
//                while (k > 0 && !queue.isEmpty()) {
//                    InventoryResponse body = (InventoryResponse) queue.poll();
//                    if (!gather.containsKey(body.getId())) {
//                        gather.put(body.getId(),new BatchInventoryResponse());
//                    }
////                    log.info("thread{},idx{}, consumer body{}", Thread.currentThread().getName(), idx, body);
//                    gather.get(body.getId()).add(body);
//                    k--;
//                }
//                log.info("consume:->{}", k);
//                for (Map.Entry<Integer, BatchInventoryResponse> ga : gather.entrySet()) {
//                    BatchInventoryResponse list = ga.getValue();
//                    log.info("[inner list]->{}", list);
//                    List<InventoryListTest> tests=list.convert();
//
////                    AtomicInteger num =new AtomicInteger();
//                    for (int i = 0; i < list.size(); i++) {
////                        num.getAndIncrement();
//                        list.get(i).setResponse("success");
//                        out.getAndIncrement();
////                        cnt.get(list.get(i)).decrementAndGet();
//                    }
//                    cnt.get(ga.getKey()).getAndAdd(-list.size());
//
//                }
//                log.info("in qps->{},out qps->{}", in, out);
//                log.info("[partition:{}] cnt->{}", partition, cnt.toString());
////                System.out.println("end");
//            } catch (Exception e) {
//                log.error("this is error->{}", e.getMessage());
//            } finally {
//
//            }
//        }
//    }

    //    private Queue<InventoryBody> msgs= Queues.newConcurrentLinkedQueue();
    void push(InventoryResponse inventoryBody) {
//        cnt.get(inventoryBody.getId()).getAndIncrement();
        in.getAndIncrement();
//        maps.get(inventoryBody.getId() % k).offer(inventoryBody);
        executorService.execute(() -> {
            maps.get(inventoryBody.getId() % k).offer(inventoryBody);
        });
    }

    public void consumer() {

    }

    public void test2() {
        int thread=10000;
        long current = System.currentTimeMillis();
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(thread);
        for (int i = 0; i < thread; i++) {
            new Thread(() -> {
                int times = 1;
                while (times-- > 0) {
                    InventoryListTest inventoryListTest = new InventoryListTest();
                    inventoryListTest.setCommodityId(random.nextInt(10));
                    inventoryListTest.setTestIdx(random.nextInt(1000));
                    mapper.insertSelective(inventoryListTest);
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        log.info("cost time->{}", end - current);
    }

    public void test() {
        int thread=10000;
        Timer timer = new Timer();
        PusherConsumer pusherConsumer = new PusherConsumer();
        int t = 10;
        for (int i = 0; i < k; i++) {
            timer.schedule(new MyTask(i), 0, 200);
        }
        int idx = 0;
        Random random = new Random();
        for (int i = 0; i < t; i++) {
            cnt.put(i, new AtomicInteger(0));
        }
        CountDownLatch countDownLatch = new CountDownLatch(thread);
        long current = System.currentTimeMillis();
        for (int i = 0; i < thread; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int idxi = idx;
                    int times = 1;
                    while (times-- > 0) {
                        idxi++;
                        InventoryResponse body = new InventoryResponse();
                        int idx = increaseIdx.getAndIncrement();
                        body.setIdx(idx);
//                        increaseIdx.getAndIncrement();
                        int id = random.nextInt(10);
                        body.setId(id);
//                        cnt.get(id).getAndIncrement();
//                        body.setCondition(new CountDownLatch(1));
//                        log.info("id:{}", body.getId());
//                        try {
//                            Thread.sleep(random.nextInt(300));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        pusherConsumer.push(body);
                        body.getResponse();
                    }
                    countDownLatch.countDown();
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        log.info("cost time->{}", end - current);
        int a = 10;
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        PusherConsumer pusherConsumer = new PusherConsumer();
        int t = 10;
        for (int i = 0; i < 4; i++) {
            timer.schedule(new MyTask(i), 0, 200);
        }
        int idx = 0;
        Random random = new Random();
        for (int i = 0; i < t; i++) {
            cnt.put(i, new AtomicInteger(0));
        }
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int idxi = idx;
                    int times = 1000;
                    while (times-- > 0) {
                        idxi++;
                        InventoryResponse body = new InventoryResponse();
                        int idx = increaseIdx.getAndIncrement();
                        body.setIdx(idx);
//                        increaseIdx.getAndIncrement();
                        int id = random.nextInt(10);
                        body.setId(id);
                        cnt.get(id).getAndIncrement();
//                        body.setCondition(new CountDownLatch(1));
//                        log.info("id:{}", body.getId());
                        try {
                            Thread.sleep(random.nextInt(300));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pusherConsumer.push(body);
                        body.getResponse();
                    }
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int a = 10;
    }

}
