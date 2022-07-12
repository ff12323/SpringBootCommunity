package com.newcoder.community.community.config;

import com.newcoder.community.community.controller.interceptor.DemoAlphaInterceptor;
import com.newcoder.community.community.controller.interceptor.LoginTicketInterceptor;
import com.newcoder.community.community.controller.interceptor.StaticDateInterceptor;
import com.newcoder.community.community.service.StaticDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 废弃原因：这是登录检查的拦截器配置，使用Spring Security代替。
 * 废弃方法：把LoginRequiredInteceptor注释掉
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer { //想要配拦截器，需要实现这个接口

    @Autowired
    private DemoAlphaInterceptor demoAlphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    private LoginRequiredInteceptor loginRequiredInteceptor;


    @Autowired
    private StaticDateInterceptor staticDateInterceptor;

    @Override //注册接口
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(demoAlphaInterceptor) //默认拦截一切请求
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")  // /**表示static目录下的所有文件夹
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor) //所以请求都要使用拦截器
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

//        registry.addInterceptor(loginRequiredInteceptor)
//                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(staticDateInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
