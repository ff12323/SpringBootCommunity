package com.newcoder.community.community.dao;


import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary //接口类，有多个实现时。优先选择这个。
public class DemoAlphaDaoMybatisImpl implements  DemoAlphaDao {
    @Override
    public String select() {
        return "Mybatis Impl";
    }
}
