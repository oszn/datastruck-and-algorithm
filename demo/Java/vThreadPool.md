# 线程池

所有的资源类池,感觉思想类似,将资源的输入作为队列.

线程池，管理线程资源。

控制线程的增删改查过程，选择线程的执行过程。

## 核心部分

目前线程池，只支持核心线程数，以及设置最大的任务数目。

线程池分为2个重要部分。

1. 线程资源。
2. 任务队列。



线程资源，也就是需要创建线程，而对于线程而言，我们需要复用线程资源，避免系统调用重写创建的过程。

```java
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
```



任务队列则是，储存Runnable的队列，线程执行过程其实在消耗队列，完成任务执行。

```java
    public interface MyRunnable{
        public void run();
    }
     public void submit(MyRunnable runnable) {
//        queue.add(runnable);
//        lock.l
        if (nowSize.get() < coreSize) {
            Worker worker = new Worker();
            worker.thread.start();
            nowSize.getAndIncrement();
        } else {

        }
        if (queue.size() >= queueSize) {
            System.out.println("full");
        } else {
            queue.add(runnable);
        }
    }
```

## 控制线程

本代码部分，控制线程资源方式有2种。

1. 直接控制资源的开关。
2. 直接使用线程函数控制。