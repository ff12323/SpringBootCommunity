package com.newcoder.community.securityDemo.dao;

import com.newcoder.community.securityDemo.DemoApp;
import com.newcoder.community.securityDemo.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = DemoApp.class)
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @Test
    public void selectById() {
        System.out.println(userMapper.selectById(103));
    }

    @Test
    public void selectByName() {
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

}