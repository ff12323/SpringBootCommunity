package com.newcoder.community.community;

import com.newcoder.community.community.NewCoderCommunityApplication;
import com.newcoder.community.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以赌博，可以嫖娼，可以吸毒，可以开票。哈哈哈！";
        String res = sensitiveFilter.filter(text);
        System.out.println(res);
        text = "这里可以♥赌♥博♥，可以♥嫖♥娼♥，可以♥吸♥毒♥，可以♥开♥票♥。哈哈哈！";
        res = sensitiveFilter.filter(text);
        System.out.println(res);
        text = "这里可以赌博博，可以嫖娼，可以吸毒开票，可以开票。哈哈哈！杀人杀";
        res = sensitiveFilter.filter(text);
        System.out.println(res);
    }
}
