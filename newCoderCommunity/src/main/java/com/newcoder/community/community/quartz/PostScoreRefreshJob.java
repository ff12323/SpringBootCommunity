package com.newcoder.community.community.quartz;

import com.newcoder.community.community.entity.DiscussPost;
import com.newcoder.community.community.service.DiscussPostService;
import com.newcoder.community.community.service.ElasticsearchService;
import com.newcoder.community.community.service.LikeService;
import com.newcoder.community.community.util.CommunityConstant;
import com.newcoder.community.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job {

    public static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

//    @Autowired
//    private ElasticsearchService elasticsearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("牛客纪元初始化失败！",e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //因为每个key都要算一下，要反复地做这个操作。不是一下就能处理完的
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0){
            logger.info("任务取消！没有需要刷新的帖子！");
            return;
        }

        logger.info("任务开始！正在刷新帖子分数：" + operations.size());
        while (operations.size()>0){
            this.refresh((Integer) operations.pop());
        }
        logger.info("任务结束！帖子分数刷新完毕：" );
    }


    private void refresh(int postId){
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        if(discussPost == null){
            logger.error("改帖子不存在：id = " +  postId);
            return;
        }

        //是否加精
        boolean wonderful = discussPost.getStatus() == 1;
        //评论数量
        long likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST,postId);
        //评论数量
        int commentCount = discussPost.getCommentCount();

        //计算权重
        double w = (wonderful?75:0) + commentCount*10 + likeCount*2;
        //分数 = 权重 + 距离天数
        double score = Math.log10(Math.max(w,1))
                + (discussPost.getCreateTime().getTime() - epoch.getTime()) /(1000*3600*24);
        //更新帖子分数
        discussPostService.updateScore(postId,score);

        //同步Es的搜索数据
//        discussPost.setScore(score);
//        elasticsearchService.saveDiscussPost(discussPost);
    }
}
