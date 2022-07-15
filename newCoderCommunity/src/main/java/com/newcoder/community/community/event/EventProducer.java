package com.newcoder.community.community.event;


import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.community.entity.Event;
import com.newcoder.community.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;


    /**
     * 发布事件到Kafka消息队列，等待异步处理
     * @param event
     */
    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
