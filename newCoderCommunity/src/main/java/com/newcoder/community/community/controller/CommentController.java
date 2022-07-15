package com.newcoder.community.community.controller;

import com.newcoder.community.community.entity.Comment;
import com.newcoder.community.community.entity.Event;
import com.newcoder.community.community.event.EventProducer;
import com.newcoder.community.community.service.CommentService;
import com.newcoder.community.community.service.DiscussPostService;
import com.newcoder.community.community.util.CommunityConstant;
import com.newcoder.community.community.util.HostHolder;
import com.newcoder.community.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);


        //触发评论事件
        Event event = new Event();
        event.setUserId(comment.getUserId());
        event.setEntityId(discussPostId);
        //评论帖子：通知发帖人；评论评论：通知评论人
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            event.setEntityUserId(discussPostService.findDiscussPostById(discussPostId).getUserId());
        }else {
            event.setEntityUserId(comment.getTargetId());
        }
        event.setTopic(TOPIC_COMMENT);
        event.setEntityType(comment.getEntityType());
        eventProducer.fireEvent(event);


        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,discussPostId);


        return "redirect:/discuss/detail/"+ discussPostId;
    }


}
