package com.newcoder.community.community.service;

import com.newcoder.community.community.util.RedisKeyUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class StaticDateService {

    @Autowired
    private RedisTemplate redisTemplate;

    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

    public void setUV(String ip,Date date){
        String key = RedisKeyUtil.getUVKey(sf.format(date));
        redisTemplate.opsForHyperLogLog().add(key,ip);
    }

    public long getUV(Date start, Date end){
        String key = RedisKeyUtil.getUVKey("union");
        List<String> listKey = new ArrayList<>();
        while (start.compareTo(end) <=0 ){
            listKey.add(RedisKeyUtil.getUVKey(sf.format(start)));
            start = DateUtils.addDays(start,1);
        }

        return redisTemplate.opsForHyperLogLog().union(key,listKey.toArray());
    }


}
