package com.newcoder.community.community.util;

//util包专门存放工具类。MailClient把发邮件的事委托给新浪去做，相对于一个客户端

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component //Spirng去管理，且是一个通用的bean，在那个层次都能用。
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromName; //发件人

    public void sendMail(String to,String title,String content){
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(fromName);
            messageHelper.setTo(to);
            messageHelper.setSubject(title);
            messageHelper.setText(content,true); //支持html文本
            javaMailSender.send(messageHelper.getMimeMessage());
        }catch(MessagingException e){
            logger.error("发送邮件失败" + e.getMessage());
        }
    }
}
