package com.newcoder.community.community.config;

//配置类，注解bean声明。在容器中装配一个第三方的bean，那个bean在jar包里。不能去加注解，那是别人写的，不能轻易改。

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration //表示这是配置类
public class DemoAlphaConfig {

    @Bean //声明第三方bena，方法的名字就是bean的名字。该方法返回的对象将被装配到容器里
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }
}
