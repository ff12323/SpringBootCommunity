package com.newcoder.community.community.controller.advice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    //返回的对象就是装配到容器中的bean;
    // 要想具备访问数据库的能力，需要创建连接。连接是由连接工厂创建的。声明参数，Spring会自动注入进来
    // 我们写这个template配什么呢？主要是序列化的方式，因为我们写的程序时Java程序，我们得到的数据时Java类型的数据。
    //最终我们要把数据存到Redis数据库里，需要指定一种序列化的方式（数据转换的方式）。
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string()); //参数里是返回一个序列化字符串的序列化器。
        // 设置普通的value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet(); //设置完触发生效

        return template;
    }
}
