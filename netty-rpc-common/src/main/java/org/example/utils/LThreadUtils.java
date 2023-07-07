package org.example.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public enum LThreadUtils {
    Instance;

    public ThreadPoolExecutor createThreadPool(String name,int corePoolSize,int maximumPoolSize){
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(30),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable task) {
                        return new Thread(task, "netty-rpc-chat-" + name + "-" + task.hashCode());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
        return threadPool;
    }
}
