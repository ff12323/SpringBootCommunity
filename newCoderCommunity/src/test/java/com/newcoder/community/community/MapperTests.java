package com.newcoder.community.community;


import com.newcoder.community.community.NewCoderCommunityApplication;
import com.newcoder.community.community.dao.*;
import com.newcoder.community.community.entity.*;
import com.newcoder.community.community.util.CommunityConstant;
import com.newcoder.community.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class MapperTests implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;


    @Test
    public void testSelect(){
        User user=userMapper.selectById(101);
        System.out.println(user);

        user=userMapper.selectByName("liubei");
        System.out.println(user);

        user=userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test-fucku");
        user.setPassword("12345");
        user.setSalt("aaa");
        user.setEmail("aa@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://images.nowcoder.com/head/111t.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"ffffff");
        System.out.println(rows);
    }

    @Test
    public void selectPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for(DiscussPost post:list){
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostsRows(149);
        System.out.println(rows);
    }


    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*60*60 ));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc",1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectCommentsByEntity(){

        int count = commentMapper.selectCountByEntity(1,228);
        System.out.println("帖子总数是：" + count);

        List<Comment> lists = commentMapper.selectCommentsByEntity(1,228,0,10);
        for (Comment comment:lists){
            System.out.println(comment.toString());
        }

    }

    @Test
    public void testInsertComment(){
        Comment comment = new Comment();
        User user = userMapper.selectById(152);
        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }

        comment.setUserId(user.getId());
        comment.setEntityType(ENTITY_TYPE_POST);
        comment.setTargetId(0);

        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(285);
        if(discussPost == null){
            throw new IllegalArgumentException("帖子不存在");
        }
        comment.setEntityId(discussPost.getId());

        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment.setContent("public void testInsertComment(){" + CommunityUtil.generateUUID());

        int res = commentMapper.insertComment(comment);
        System.out.println(res);
        return;
    }

    @Test
    public void testUpdateDiscussPostCommentCount(){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(285);
        if(discussPost == null){
            throw new IllegalArgumentException("帖子不存在");
        }
        int count = discussPost.getCommentCount() + 1;
        int res = discussPostMapper.updateDiscussPostCommentCount(discussPost.getId(),count);
        System.out.println(res);
        return;
    }

    @Test
    public void testMessageMapper(){
        User user = userMapper.selectById(111);
        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }
        System.out.println("该用户的最新消息列表数量："+ messageMapper.selectConversationCount(user.getId()));
        List<Message> list = messageMapper.selectConversations(user.getId(),0,10);
        for(Message message:list){
            System.out.println(message);
        }
        System.out.println("---------------------");
        System.out.println("某个会话的消息数量："+ messageMapper.selectLetterCount("111_112"));
        list = messageMapper.selectLetters("111_112",0,10);
        for(Message message:list){
            System.out.println(message);
        }
        System.out.println("---------------------");
        System.out.println("该用户所有的未读消息数量："+ messageMapper.selectLetterUnreadCount(user.getId(),null));
        System.out.println("特定会话未读消息数量："+messageMapper.selectLetterUnreadCount(user.getId(),"111_112"));
    }
}
