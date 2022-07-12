package com.newcoder.community.community;


import com.newcoder.community.community.entity.DiscussPost;
import com.newcoder.community.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class CaffeineTests {

    @Autowired
    private DiscussPostService  postService;

//    @Test
//    public void initDataForTest(){
//        for (int i = 0; i < 300000; i++) {
//            DiscussPost post = new DiscussPost();
//            post.setUserId(111);
//            post.setTitle("求职计划" + i);
//            post.setContent("求职计划" + i);
//            post.setCreateTime(new Date());
//            post.setScore(Math.random()*2000);
//            postService.addDiscussPost(post);
//            System.out.println(i);
//        }
//    }


    @Test
    public void testCache(){
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,0));
    }



}
