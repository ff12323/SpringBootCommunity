package com.newcoder.community.community.dao.elasticsearch;


import com.newcoder.community.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//继承，泛型：处理的实体类是什么，主键是什么类型？
//声明好，Spring就会自动帮我们实现。
@Repository
public interface DiscussPostRepository
//        extends ElasticsearchRepository<DiscussPost,Integer>
{

    List<DiscussPost> findByTitle(String title);
}
