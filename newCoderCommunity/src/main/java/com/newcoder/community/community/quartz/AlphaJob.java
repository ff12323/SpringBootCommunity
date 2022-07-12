package com.newcoder.community.community.quartz;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlphaJob implements Job {

    public static final Logger logger = LoggerFactory.getLogger(AlphaJob.class);

    public static int count = 0;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("alpha Job: " + count++);
    }

}
