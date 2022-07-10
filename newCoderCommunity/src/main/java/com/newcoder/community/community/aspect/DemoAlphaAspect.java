package com.newcoder.community.community.aspect;


import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DemoAlphaAspect {

    //execution固定关键字，第一个*表示任意的方法返回值，包名下的*所有的类*所有的方法(..)所有的参数。
    @Pointcut("execution(* com.newcoder.community.community.service.*.*(..))")
    public void pointcut(){

    }

/*    //以此方法为切点
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    //即在前面也在后面进行处理
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object obj = joinPoint.proceed(); //实际调用代码。
        System.out.println("around after");
        return obj;
    }*/

}
