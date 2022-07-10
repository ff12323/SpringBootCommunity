package com.example.multimodule.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest({"service.message=Hello","service.zyhInfo=zyhWorld"})
class MyServiceTest {

    @Autowired
    private MyService myService;

    @Test
    public void contextLoads(){
        System.out.println(myService.message());
    }

    @SpringBootApplication
    static class TestConfiguration{

    }
}