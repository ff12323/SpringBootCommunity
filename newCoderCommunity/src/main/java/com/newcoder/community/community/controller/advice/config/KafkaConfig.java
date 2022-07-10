package com.newcoder.community.community.controller.advice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {


    @Bean
    public KafkaTemplate<String,Object> kafkaTemplate(ProducerFactory<String,Object> producerFactory
    ,ConsumerFactory<String,Object> consumerFactory){
        KafkaTemplate<String,Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setConsumerFactory(consumerFactory);
        kafkaTemplate.setDefaultTopic("zyhMsg");
        return kafkaTemplate;
    }



}
