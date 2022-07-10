package com.newcoder.community.community;

import com.newcoder.community.community.NewCoderCommunityApplication;
import com.newcoder.community.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("2747768001@qq.com","a test","fcku");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","context.setVariable");

        String content = templateEngine.process("/mail/demo",context);

        System.out.println(content);

        mailClient.sendMail("2747768001@qq.com","HTML TEST",content);

    }
}
