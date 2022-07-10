package com.newcoder.community.community.dao;

import com.newcoder.community.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //根据类型来查询，帖子的评论？评论的评论？
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    int selectCountByEntity(int entityType,int entityId);


    int insertComment(Comment comment);
}

