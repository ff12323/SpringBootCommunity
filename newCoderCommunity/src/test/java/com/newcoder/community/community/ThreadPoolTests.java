package com.newcoder.community.community;


import com.newcoder.community.community.service.DemoAlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class ThreadPoolTests {

    public static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);


    //Spring 普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //Spring 可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Test
    public void testExecutorService() throws InterruptedException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello executorService");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        Thread.sleep(10000);
    }

    @Test
    public void testScheduledExecutorService() throws InterruptedException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ScheduledExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);

        Thread.sleep(30000);
    }


    @Test
    public void testThreadPoolTaskExecutor() throws InterruptedException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.execute(task);
        }

        Thread.sleep(10000);
    }


    @Test
    public void testThreadPoolTaskScheduler() throws InterruptedException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskScheduler");
            }
        };

        taskScheduler.scheduleAtFixedRate(task,1000);


        Thread.sleep(10000);
    }



    @Autowired
    private DemoAlphaService demoAlphaService;

    //Spring线程池：注解方式使用
    @Test
    public void testAsyncMethod() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            demoAlphaService.execute();
        }

        Thread.sleep(10000);
    }

    //Spring定时任务线程池，自动就会调用（只要程序在跑）
    @Test
    public void testScheduled() throws InterruptedException {
//        for (int i = 0; i < 10; i++) {
//            demoAlphaService.execute2();
//        }

        Thread.sleep(30000);
    }

}
