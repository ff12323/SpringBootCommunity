package com.newcoder.community.community.dao;

import com.newcoder.community.community.entity.User;
import org.apache.ibatis.annotations.Mapper;


//Mybatis 开发 通常称之为Mapper，只要写接口，不用写类。
//我们需要写一个配置文件，配置文件里写SQL语句，mybatis底层会帮我们实现接口的实现类。
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id,int status);

    int updateHeader(int id,String headerUrl);

    int updatePassword(int id,String password);
}
