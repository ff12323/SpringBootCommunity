package com.newcoder.community.community.config;

import com.newcoder.community.community.quartz.AlphaJob;
import com.newcoder.community.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置-->第一次使用保存到数据库里 --> 以后不再访问，读数据库
@Configuration
public class QuartzConfig {

    //FactoryBean可以简化bean的实例化过程：
    //1、通过FactoryBean封装了Bean的实例化过程
    //2、将FactoryBean装配到Spring容器里
    //3、将FactoryBean注入给其他的Bean。
    //4、该Bean得到的FactoryBean所管理的对象实例

    @Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setGroup("alphaJobDetailGroup");
        factoryBean.setName("alphaJob");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean alphaJobTrigger(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaJobTriggerGroup");
        factoryBean.setRepeatInterval(10000);
        factoryBean.setRepeatCount(20);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }


    @Bean
    public JobDetailFactoryBean refreshPostScoreJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setGroup("communityJobDetailGroup");
        factoryBean.setName("refreshPostScoreJob");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean refreshPostScoreJobTrigger(JobDetail refreshPostScoreJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(refreshPostScoreJobDetail);
        factoryBean.setName("refreshPostScoreTrigger");
        factoryBean.setGroup("communityJobTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5); //每5分钟计算一次
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
