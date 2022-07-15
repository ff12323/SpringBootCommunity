package com.newcoder.community.community.event;


import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.community.entity.Event;
import com.newcoder.community.community.entity.Message;
import com.newcoder.community.community.service.MessageService;
import com.newcoder.community.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EventConsumer implements CommunityConstant {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE})
    public void handlerSystemEvents(ConsumerRecord record){
        String topic = record.topic();
        String jsonEvent = (String) record.value();
        Event event = JSONObject.parseObject(jsonEvent,Event.class);

        Message message = new Message();
        message.setConversationId(topic);
        message.setCreateTime(new Date());
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setContent(jsonEvent);

        messageService.addMessage(message);
    }
}
