package com.newcoder.community.community.controller.interceptor;

import com.newcoder.community.community.service.StaticDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class StaticDateInterceptor implements HandlerInterceptor {

    @Autowired
    private StaticDateService staticDateService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ip = request.getRemoteAddr();
        staticDateService.setUV(ip,new Date());


        return true;
    }
}
