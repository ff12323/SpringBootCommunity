package com.newcoder.community.securityDemo.service;


import com.newcoder.community.securityDemo.dao.UserMapper;
import com.newcoder.community.securityDemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByName(username);
    }
}
