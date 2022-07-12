package com.newcoder.community.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.newcoder.community.community.dao.DiscussPostMapper;
import com.newcoder.community.community.util.SensitiveFilter;
import com.newcoder.community.community.entity.DiscussPost;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {


    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //Caffeine核心接口：Cache，LoadingCache（同步），AsyncLoadingCache（异步）

    //帖子列表的缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子列表的缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    //提供一个查询数据库得到数据的办法（还没有缓存数据时）
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if(StringUtils.isBlank(key)){
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if(params == null || params.length !=2){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        //二级缓存：Redis ---> mysql （如果要做二级缓存）

                        logger.debug("load post from DB.");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数的缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostsRows(key);
                    }
                });
    }

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int searchMode){

        //只有当首页的时候 && 且按热门搜帖子时才进行缓存
        if(userId == 0 && searchMode == 1){
            return postListCache.get(offset + ":" + limit); //以第几页作为key搜索
        }

        logger.debug("load post from DB.");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit, searchMode);
    }

    public int findDiscussPostsRows(int userId){
        //只有首页查询的时候，进行缓存
        if(userId == 0 ){
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");
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

    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }

}
