package com.newcoder.community.community.controller;


import com.newcoder.community.community.annotation.LoginRequired;
import com.newcoder.community.community.entity.User;
import com.newcoder.community.community.service.FollowService;
import com.newcoder.community.community.service.LikeService;
import com.newcoder.community.community.service.UserService;
import com.newcoder.community.community.util.CommunityConstant;
import com.newcoder.community.community.util.CommunityUtil;
import com.newcoder.community.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path="/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImg, Model model){
        if(headerImg == null){
            model.addAttribute("error","图片呢??????");
            return "/site/setting";
        }

        //上传的文件名字需要处理，有很多人上传，文件名字可能相同。我们生成一个随机的名字
        String filename = headerImg.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确！！！");
            return "/site/setting";
        }

        //生成随机的文件名
        filename = CommunityUtil.generateUUID() + suffix;
        //确定一下文件的存放路径
        File dest = new File(uploadPath+"/"+filename);
        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败！！" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发送异常！",e);
        }

        //更新当前用户的头像的路径。不是什么D盘的路径，本地路径。我们需要提供web访问路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    /**
     * 这是一个比较特别的方法，返回的是图片，而就是二进制流。需要手动的向浏览器输出。
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try(
                FileInputStream fis = new FileInputStream(fileName);
                ) {
            OutputStream os = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int b = 0;
            while ( (b = fis.read(buffer)) != -1 ){
               os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("图片获取失败！！！"+e.getMessage());

        }
    }


    @LoginRequired
    @RequestMapping(path = "/changePassword",method = RequestMethod.POST)
    public String changePassword(Model model,String oldPassword, String newPassword,String newPassword2){

        if(! newPassword.equals(newPassword2)){
            model.addAttribute("newPasswordMsg","两次输入密码不一致");
            return "/site/setting";
        }

        if( StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","输入密码不能为空");
            return "/site/setting";
        }

        User user = hostHolder.getUser();
        if( userService.checkPassword(user.getId(),oldPassword)){
            userService.updatePassword(user.getId(),newPassword);
        }else{
            model.addAttribute("passwordMsg","输入的密码不是原密码！");
            return "/site/setting";
        }
        return "redirect:/logout"; //重定向默认是get请求
    }



    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new IllegalArgumentException("该用户不存在！");
        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //查询关注数量
        long followeeCount =followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        //分数数量
        long followerCount =followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //当前用户对此用户，是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }


}
