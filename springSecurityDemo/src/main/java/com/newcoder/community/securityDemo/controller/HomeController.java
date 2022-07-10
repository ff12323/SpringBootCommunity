package com.newcoder.community.securityDemo.controller;


import com.newcoder.community.securityDemo.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {


    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndex(Model model){
        //认证成功后，结果会通过SecurityContextHolder存入SecurityContext当中。（底层有一个Filter专门干这个事）
       Object res =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       if(res instanceof User){
           model.addAttribute("loginUser",res);
       }

        return "/index";
    }



    @RequestMapping(path = "/loginPage")
    public String loginPage(){
        return "/loginPage";
    }

    @RequestMapping(path = "/detail",method = RequestMethod.GET)
    public String detail(){
        return "/detail";
    }

    @RequestMapping(path = "/privateMsg",method = RequestMethod.GET)
    public String privateMsg(){
        return "/privateMsg";
    }

    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String denied(){
        return "/404";
    }

    @RequestMapping(path = "/admin",method = RequestMethod.GET)
    public String admin(){
        return "/admin";
    }

}
