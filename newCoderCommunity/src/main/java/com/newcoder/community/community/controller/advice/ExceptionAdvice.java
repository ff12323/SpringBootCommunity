package com.newcoder.community.community.controller.advice;


import com.newcoder.community.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//这个组件 只去 扫描带有Controller注解的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    //当发送异常时，可以把参数传过来。
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发送异常：" + e.getMessage());
        for(StackTraceElement element: e.getStackTrace()){
            logger.error(element.toString());
        }

        //我们需要判断一下，这里时普通请求，还是异步请求（返回的是JSON,不是页面）
        String xRequestedWith = request.getHeader("x-requested-with");
        if(xRequestedWith.equals("XMLHttpRequest")){
            // application/json 则向浏览器返回一个字符串，浏览器自动转换为json对象
            response.setContentType("application/plain;charset=utf-8"); //返回普通字符串，但是需要人为的转换为json对象。比如$.parseJSON
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!,消息来自统一异常处理：@ControllerAdvice ExceptionAdvice"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }

}
