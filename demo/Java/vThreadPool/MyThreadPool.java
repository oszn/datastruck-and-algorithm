package thread.api.threadpool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//https://blog.csdn.net/qq_46312987/article/details/124470883
//https://zhuanlan.zhihu.com/p/62394330
public class MyThreadPool {
    public class ThreadObj {
        Worker thread;
        ThreadObj next;
    }

    public interface MyRunnable {
        public void run();
    }

    Set<Worker> workers = new HashSet<>();

    Lock lock;
    Condition empty;
    Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public class Worker implements Runnable {
        Thread thread;

        public Worker() {
            thread = new Thread(this);
        }

        @Override
        public void run() {
            try {
                while (active) {
                    MyRunnable task = queue.take();
                    task.run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    int queueSize = 0;
    int coreSize = 10;
    AtomicInteger nowSize = new AtomicInteger(0);
    ArrayBlockingQueue<MyRunnable> queue;
    ConcurrentLinkedQueue<Worker> concurrentLinkedQueue;

    public void initThread() {

    }

    public MyThreadPool(int coreSize, int size) {
        queue = new ArrayBlockingQueue<>(size);
        lock = new ReentrantLock();
        concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        empty = lock.newCondition();
        this.coreSize = coreSize;
        active = true;
        queueSize = size;
    }

    public void submit(MyRunnable runnable) {
//        queue.add(runnable);
//        lock.l
        if (nowSize.get() < coreSize) {
            Worker worker = new Worker();
            worker.thread.start();
            concurrentLinkedQueue.add(worker);
            nowSize.getAndIncrement();
        } else {

        }
        if (queue.size() >= queueSize) {
            System.out.println("full");
        } else {
            queue.add(runnable);
        }
    }

    public synchronized void shutDown() {
//        for (Worker worker : concurrentLinkedQueue) {
//            worker.thread.interrupt();
//        }
        active=false;
        concurrentLinkedQueue.clear();
        nowSize = new AtomicInteger(0);
        queue.clear();
        concurrentLinkedQueue = null;
        queue = null;
    }

    public void execute() {

    }

    public static void main(String[] args) throws InterruptedException {
        MyThreadPool myThreadPool = new MyThreadPool(5, 100);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            myThreadPool.submit(new MyRunnable() {
                @Override
                public void run() {
                    System.out.println(finalI);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        Thread.sleep(10000);
//        myThreadPool.setActive(false);
        myThreadPool.shutDown();
    }

}
