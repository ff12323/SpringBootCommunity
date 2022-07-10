package com.newcoder.community.community.controller.interceptor;

import com.newcoder.community.community.entity.LoginTicket;
import com.newcoder.community.community.entity.User;
import com.newcoder.community.community.service.UserService;
import com.newcoder.community.community.util.CookieUtil;
import com.newcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 从Cookie中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");

        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效。 凭证不为空，并且状态有效，并且超时时间还没过。
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有的用户 这个多线程的，每个线程都要存一份，互不干扰
                //使用HostHoder工具取持有用户，为什么其能持有？我们把数据存到当前线程对应的map里。只要这个请求没有处理完，则这个线程一直还在。
                //当请求处理完，服务器向浏览器做出响应后，这个线程被销毁。所以处理过程中，数据一直都在。
                hostHolder.setUser(user);
            }
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if( user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理掉数据
        hostHolder.clear();
    }
}
