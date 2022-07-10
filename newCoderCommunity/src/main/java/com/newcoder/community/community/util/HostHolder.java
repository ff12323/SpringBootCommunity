package com.newcoder.community.community.util;

import com.newcoder.community.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替Session对象，
 */
@Component
public class HostHolder {
    // ThreadLocal提供一个多线程的方法，往里面存值。get和set以及remove方法都是以当前线程为key，在map里存和取值。达到线程隔离
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
