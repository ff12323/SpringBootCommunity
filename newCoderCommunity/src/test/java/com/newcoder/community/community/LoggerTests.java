package com.newcoder.community.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class LoggerTests {

    //getLogger 传入一个类，这个类名就是logger的名字。通常把当前类传入，这样不同的logger在不同的类下就有不同的区别。
    //打印到日志上，我们也能知道这个logger是属于那个地方的logger
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());


        //默认是打印到控制台
        logger.debug("debug log");//使用：1、try catch捕获到异常，记录日志
        logger.info("info log"); //启用线程池、定时任务等。担心有问题，记录普通的日志
        logger.warn("warn log");
        logger.error("error log");


    }

}
