package com.newcoder.community.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

import javax.annotation.PostConstruct;

@SpringBootApplication() //表示这是一个配置文件。
// 由3个注解组成@Configuration,
// @EnableAutoConfiguration，表示启动自动配置。不用配置什么就能运行。
// @ComponentScan，组件扫描，自动扫描某些包下的某些bean，装配到容器里。问：扫描那些呢？答：会扫描配置类所在的包，以及子包下的bean。
public class NewCoderCommunityApplication {


    @PostConstruct
    public void init(){
        //Es搜索解决netty启动冲突的问题
        //see Netty4Utils
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    public static void main(String[] args) {
        /*
           自动帮我们创造Spring 容器，自动取扫描某些包下的bean，将bean装配到容器里。
           那么那些bean会被扫描到呢？ 我们需要看 NewCoderCommunityApplication类（如同一个配置文件）
         */
        SpringApplication.run(NewCoderCommunityApplication.class, args);
    }

}
