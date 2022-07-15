package org.sringCloudDemos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController
@EnableEurekaClient
@RequestMapping("/")
public class UserServicesApp {
    private static final Logger logger = LoggerFactory.getLogger(UserServicesApp.class);

    @GetMapping("/")
    public String index1(){
        logger.debug("访问首页！");
        return "User Service Home";
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServicesApp.class,args);
    }
}
