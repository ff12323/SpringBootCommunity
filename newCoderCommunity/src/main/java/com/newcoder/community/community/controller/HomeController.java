package com.newcoder.community.community.controller;

import com.newcoder.community.community.entity.DiscussPost;
import com.newcoder.community.community.entity.Page;
import com.newcoder.community.community.entity.User;
import com.newcoder.community.community.service.DiscussPostService;
import com.newcoder.community.community.service.LikeService;
import com.newcoder.community.community.service.UserService;
import com.newcoder.community.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path ="/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "searchMode",defaultValue = "0") int searchMode){
        //Spring MVC里面 model是由DispatcherServlet初始化的，page也是由其初始化且数据注入也是。
        //方法调用前，Spring MVC会自动实例化Model和Page，并将Page给注入Model里。
        //所以，在thymeleaf里，我们可以直接访问page对象里的数据。

        //设置 帖子的总数量
        page.setRows(discussPostService.findDiscussPostsRows(0));
        page.setPath("/index?searchMode=" + searchMode);



        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),searchMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if(list != null){
            for(DiscussPost post:list){
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                //点赞功能
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("searchMode",searchMode);

        //System.out.println("total:"+page.getTotal() + " rows:"+page.getRows());
        return "/index";
    }


    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }


    //权限不足：拒绝访问
    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String denied(){
        return "/error/404";
    }


}
