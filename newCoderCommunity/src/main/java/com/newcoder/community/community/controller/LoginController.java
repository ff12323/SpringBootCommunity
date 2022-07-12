package com.newcoder.community.community.controller;

import com.google.code.kaptcha.Producer;
import com.newcoder.community.community.entity.User;
import com.newcoder.community.community.service.UserService;
import com.newcoder.community.community.util.CommunityConstant;
import com.newcoder.community.community.util.CommunityUtil;
import com.newcoder.community.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path="/register", method = RequestMethod.GET)
    public String getRegisterPage(){

        return "/site/register";
    }

    @RequestMapping(path="/login", method = RequestMethod.GET)
    public String getLoginPage(){

        return "/site/login";
    }

    /**
     * 规则了解：如果 register(Model model, User user)的参数不是普通的参数(比如user），Spring MVC 会将其装到Model里。
     * 如果是比较普通的类型，String，int之类的。Spring不会把其放到Model里。
     * 得到其的两种办法：
     * 1、人为地把这些参数加到Model里
     * 2、这些参数是存在与request对象里的，代码里写request.getParameter也能得到。
     * 当程序执行到html文件里，request还没有销毁，因为程序还没有结束。html也可以从request中取值。
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(path="/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            //注册成功，返回第三方页面operate-result.html。等待激活
            model.addAttribute("msg","注册成功，已发邮件，请激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }


    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int res = userService.activation(userId,code);
        if(res == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，可登录");
            model.addAttribute("target","/login");

        }else if(res == ACTIVATION_REPEAT){
            model.addAttribute("msg","不要重复激活");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败");
            model.addAttribute("target","/index");
        }

        return "/site/operate-result";
    }

    /**
     * 功能重构
     * 图片验证在哪个方法里写呢？不是在path="/login"，其返回的时登录的html。
     * 而 html里会包含一个图片的路径，浏览器会依据路径再次访问服务器获得这个图片。所以要再写一个请求。
     *  要点1：生成完验证码，服务端要把它记住。当你登录的时候，再次访问服务器时，我好验证你的验证码对不对？
     *  要点2：验证码不能存放在浏览器端，易被盗取。可以用Session
     * @param res
     */
    @RequestMapping(path="/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse res/*, HttpSession session*/){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //存验证码
        //session.setAttribute("kaptcha",text);
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        res.addCookie(cookie);
        //将验证码存入Redis,设置60秒失效
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        //将图片输出给浏览器
        res.setContentType("image/png");
        try {
            OutputStream os = res.getOutputStream();
            ImageIO.write(image,"png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败啦啦啦！"+ e.getMessage());
        }
    }

    /**
     * 功能重构：验证码存在Redis里。
     * 有两个方法路径都是一样的，这个可行的。但是前提是这两个方法的请求方法要有区别。用post，处理表单提交的数据。
     *
     * 规则了解：如果 String login(String username,xxx)的参数不是普通的参数(比如user），Spring MVC 会将其装到Model里。
     * 如果是比较普通的类型，String，int之类的。Spring不会把其放到Model里。
     * 得到其的两种办法：
     * 1、人为地把这些参数加到Model里
     * 2、这些参数是存在与request对象里的，代码里写request.getParameter也能得到。
     * 当程序执行到html文件里，request还没有销毁，因为程序还没有结束。html也可以从request中取值。
     * @param username
     * @param password
     * @param code  验证码
     * @param rememberMe 记住用户，保存时间更长
     * @param model
     * @param response
     * @return
     */
    @RequestMapping(path="/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model/*,HttpSession session*/, HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner)
    {
        //首先判断验证码对不对
        //String kaptcha =(String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误啦啦啦");
            return "/site/login";
        }


        //检查账号，密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath); //整个项目都有效
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index"; //登录成功，跳到首页
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }


    /**
     * Spring Security：退出清理认证信息
     * @param ticket
     * @return
     */
    @RequestMapping(path="/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) { //要求Spring注入cookie
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login"; //默认是get请求
    }


    @RequestMapping(path="/forget_getVerifyCode",method = RequestMethod.GET)
    public String forget_getVerifyCode(HttpSession session,String email,Model model) {
        Map<String,Object> map = userService.forget_getVerifyCode(email);
        if(map.get("emailMsg") != null){
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/forget";
        }

        session.setAttribute("forget_getVerifyCode",map.get("forget_getVerifyCode"));


        return "/site/forget";
    }

    @RequestMapping(path="/forget",method = RequestMethod.POST)
    public String forget(HttpSession session,String email,String verifyCode,String password,
                         Model model) {
        Map<String,Object> map = userService.forget(email,password);
        if(map.get("emailMsg") != null){
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/forget";
        }

        if(map.get("passwordMsg") != null){
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/forget";
        }

        if( verifyCode.equals(session.getAttribute("forget_getVerifyCode")) ){
            userService.changePasswordByEmail(email,password);
        }else{
            model.addAttribute("verifyCodeMsg","验证码不对哦哦哦！");
        }

        return "/site/forget";
    }

    @RequestMapping(path="/forget",method = RequestMethod.GET)
    public String forget() {
        return "/site/forget";
    }

}
