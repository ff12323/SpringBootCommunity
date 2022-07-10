package com.newcoder.community.community.controller.advice.config;

import com.newcoder.community.community.controller.interceptor.DemoAlphaInterceptor;
import com.newcoder.community.community.controller.interceptor.LoginRequiredInteceptor;
import com.newcoder.community.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer { //想要配拦截器，需要实现这个接口

    @Autowired
    private DemoAlphaInterceptor demoAlphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInteceptor loginRequiredInteceptor;

    @Override //注册接口
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(demoAlphaInterceptor) //默认拦截一切请求
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")  // /**表示static目录下的所有文件夹
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor) //所以请求都要使用拦截器
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(loginRequiredInteceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
