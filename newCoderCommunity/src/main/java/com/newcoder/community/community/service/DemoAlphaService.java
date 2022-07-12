package com.newcoder.community.community.service;

//Service，业务功能

import com.newcoder.community.community.dao.DemoAlphaDao;
import com.newcoder.community.community.dao.DiscussPostMapper;
import com.newcoder.community.community.dao.UserMapper;
import com.newcoder.community.community.entity.DiscussPost;
import com.newcoder.community.community.entity.User;
import com.newcoder.community.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service //扫描配置
//@Scope("prototype") //singleton 单例, prototype 每次都创建新的
public class DemoAlphaService {


    public static final Logger logger = LoggerFactory.getLogger(DemoAlphaService.class);

    @Autowired
    private DemoAlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    public  DemoAlphaService(){
        System.out.println("实例化DemoAlphaService");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化DemoAlphaService");
    }


    @PreDestroy
    public void destroy(){
        System.out.println("销毁DemoAlphaService");
    }

    //查询
    public String find(){
        return alphaDao.select();
    }


    /**
     * 功能：模拟一个业务，注册一个用户，并且自动发送一个帖子。
     * propagation注解讲解；一个事务方法A里面可能调用另一个事务方法B，以谁的事务为准？（A的？B的？新的方式？）
     * Propagation.REQUIRED：支持当前事务（外部事务，对B来说，A是外部事务。）如果不存在则创建新事务。A调B，对B来说，若A有，按A。若无，按B来。
     * REQUIRES_NEW：创建一个新的事务，并且暂停当前事务。A调B，B停止A的，按自己的来。
     * NESTED：如果当前存在事务，则嵌套在该事务中执行（B有独立的提交和回滚），否则就和REQUIRED一样。
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.email");
        user.setHeaderUrl("http://images.nowcoder.com/head/23t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello!");
        post.setContent("新人报道！！！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");


        return "0k";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() { //回调方法
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.email");
                user.setHeaderUrl("http://images.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("beta!");
                post.setContent("新人beta报道！！！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");


                return "0k";
            }
        });
    }



    //该方法在多线程环境下，被异步的调用
    @Async()
    public void execute(){
        logger.debug("execute");
    }


    //该方法在多线程环境下，被异步的调用。定时执行（程序启动，就会自动调用。无需调用方法）
    @Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2(){
        logger.debug("execute");
    }

}
