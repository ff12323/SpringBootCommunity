package com.newcoder.community.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
//@EnableScheduling //这个东西比较敏感，则没有启动
@EnableAsync
public class SpringThreadPoolConfig {
}
