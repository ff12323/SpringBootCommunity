package com.newcoder.community.community.service;

import com.newcoder.community.community.dao.DiscussPostMapper;
import com.newcoder.community.community.util.SensitiveFilter;
import com.newcoder.community.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostsRows(int userId){
        return discussPostMapper.selectDiscussPostsRows(userId);
    }


    public int addDiscussPost(DiscussPost discussPost){
        if(discussPost == null){
            throw new IllegalArgumentException("参数你不能为空！！！");
        }

        //用Spring自带的的工具。转移HTML标记；标签去掉，如果注入如：<script>xsc</script>这样的标签。可能有损坏的可能性。
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //标题与内容的敏感词。要进行过滤
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateDiscussPostCommentCount(int id,int commentCount){
        return discussPostMapper.updateDiscussPostCommentCount(id,commentCount);
    }
}
