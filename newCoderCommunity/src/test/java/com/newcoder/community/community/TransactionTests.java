package com.newcoder.community.community;


import com.newcoder.community.community.NewCoderCommunityApplication;
import com.newcoder.community.community.service.DemoAlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class TransactionTests {
    @Autowired
    private DemoAlphaService demoAlphaService;

    @Test
    public void testSave1(){
        Object obj = demoAlphaService.save1();
        System.out.println(obj);
    }

    @Test
    public void testSave2(){
        Object obj = demoAlphaService.save2();
        System.out.println(obj);
    }
}
