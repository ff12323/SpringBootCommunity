package com.newcoder.community.community.dao;

import com.newcoder.community.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);


    //@Param 可以给参数起别名，缩短。  动态SQL<if>里，且只有一个参数。则一定要起别名
    int selectDiscussPostsRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateDiscussPostCommentCount(int id,int commentCount);
}