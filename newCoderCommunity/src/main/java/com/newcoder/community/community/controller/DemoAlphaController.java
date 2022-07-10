package com.newcoder.community.community.controller;

import com.newcoder.community.community.service.DemoAlphaService;
import com.newcoder.community.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

//开发过程：Controller处理浏览器的请求,其会调用service的业务组件，业务组件会调用dao去访问数据库。它们之间相互依赖，可以用自动注入

@Controller  //需要由注解才会被扫描，该注解会被扫描。例如@Service（由@Component实现，该注解也行）、@Repository也会被扫描。
@RequestMapping("/alpha")
public class DemoAlphaController {

    @Autowired
    DemoAlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){return "hello world!";}

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){return alphaService.find();}


    @RequestMapping("/http")
    public void http(HttpServletRequest req, HttpServletResponse res){
        System.out.println(req.getMethod());
        System.out.println(req.getServletPath());
        Enumeration<String> enums = req.getHeaderNames();
        while (enums.hasMoreElements()){
            String name = enums.nextElement();
            String value = req.getHeader(name);
            System.out.println(name+": "+value);
        }
        System.out.println(req.getParameter("code"));

        //响应请求
        res.setContentType("text/html;charset=utf-8");
        try(PrintWriter writer = res.getWriter(); //java 7 语法，括号自动finally关闭资源）
        ){
            writer.write("<h1>Servlet response Test Demo</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Get请求，查询所有学生列表
    //  /students?current=1&limit=20
    @RequestMapping(path="/students", method = RequestMethod.GET)
    @ResponseBody
    public  String getStudents( //参数进一步处理，设置是否必须，默认值。
            @RequestParam(name="current", required = false,defaultValue = "1") int current,
            @RequestParam(name="limit", required = false,defaultValue = "20")int limit){

        return "some students at " + current + " pages, "+limit+" max number per page.";
    }

    //Get请求，查询单个学生
    // /students/123
    @RequestMapping(path="/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public  String getStudent( //路径变量
                               @PathVariable("id") int id ){

        return "student id is: "+ id;
    }

    //Post请求
    @RequestMapping(path="/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        return name+" "+age;
    }

    //响应HTML数据
    @RequestMapping(path="/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","tom");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(path="/school", method = RequestMethod.GET)
    public String getTeacher(Model model){
        model.addAttribute("name","first school");
        model.addAttribute("age",99);
        return "/demo/view";
    }


    //响应JSON数据 异步请求
    //Java对象--》JSON字符串--》JS对象
    @RequestMapping(path="/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","toms");
        emp.put("age",23);
        emp.put("salary",1000.0);
        return emp;
    }


    //Cookie示例

    @RequestMapping(path="/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse res){
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie（设置范围：在那些路径下有效。指定浏览器再重新访问服务器，那些路径才发cookie）
        cookie.setPath("/community/alpha"); //在这个路径和它的子路径下，才有效
        // 设置cookie生存时间。默认cookie保存在内存里，浏览器关闭则无。可以设置为保存在硬盘里，生效几天。
        cookie.setMaxAge(60*10);
        // 发送cookie
        res.addCookie(cookie);

        return "set cookie";
    }

    @RequestMapping(path="/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie is:" + code;
    }


    //Session示例
    @RequestMapping(path="/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){ //Spring MVC可以自动地帮我们创建对象并注入进来。其和Model对象。。。类似
        session.setAttribute("id",1);
        session.setAttribute("name","hello");

        return "session set";
    }

    @RequestMapping(path="/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        int id = (Integer) session.getAttribute("id");
        String name =(String) session.getAttribute("name");

        return "session get: " + id + " " + name;
    }


    //AJAX示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name + age);
        return CommunityUtil.getJSONString(0,"操作好了！");
    }

}
