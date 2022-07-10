package com.newcoder.community.community.dao;

import org.springframework.stereotype.Repository;

//Hibernate实现
@Repository("alpahHibernate") //该bean需要被扫描，@Repository 是访问数据库的bean的注解。可自定义bean的名字
public class DemoAlphaDaoHibernateImpl implements DemoAlphaDao{
    @Override
    public String select() {
        return "Hibernate Impl";
    }
}
