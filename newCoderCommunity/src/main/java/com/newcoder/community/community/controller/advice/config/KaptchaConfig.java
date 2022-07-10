package com.newcoder.community.community.controller.advice.config;


import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean //服务启动时，可以自动的装配到容器里。通过容器，可以得到一个producer实例，得到示例。里面有两个方法，可以创建验证码图片
    public Producer kaptchaProducer(){
        Properties properties = new Properties(); //不写配置文件了，直接设置好了
        properties.setProperty("kaptcha.image.width","100"); //单位像素
        properties.setProperty("kaptcha.image.height","40"); //单位像素
        properties.setProperty("kaptcha.textproducer.font.size","32");//字号
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");//黑色
        properties.setProperty("kaptcha.textproducer.char.string","0123456789abcdefghijklfmnpqrstxyz");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties); //传入配置文件，进行设置。
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
