package com.newcoder.community.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class KafkaTests {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Test
    public void f1(){
        System.out.println(kafkaTemplate);
        kafkaTemplate.sendDefault("fff");
        String res = kafkaTemplate.getDefaultTopic();
        ConsumerRecord<String,Object> rec = kafkaTemplate.receive(res,0,0);
        System.out.println(rec.value());


    }

    @Autowired
    KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","你好？");
        kafkaProducer.sendMessage("test","在吗？");
        try{
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}

@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic,content);
    }
}


@Component
class KafkaConsumer{

    /**
     * V讲解：注解写完之后，Spring就会自动地去监听里面的主题。它就是有一个线程阻塞在那，试图读取主题下的消息。
     * 无则阻塞，有则立即读取。
     * 特点：被动处理，略有延时。
     */
    @KafkaListener(topics = {"test"})
    public void handleMsg(ConsumerRecord consumerRecord){
        System.out.println("KafkaListener接收："+consumerRecord.value());
    }
}