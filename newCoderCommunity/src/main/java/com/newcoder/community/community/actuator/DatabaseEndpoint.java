package com.newcoder.community.community.actuator;


import com.newcoder.community.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Endpoint(id = "db")
public class DatabaseEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    //这个方法，通过get请求访问
    @ReadOperation
    public String checkConnection(){
        try(Connection connection = dataSource.getConnection()){
            return CommunityUtil.getJSONString(0,"获取连接成功");
        }catch (Exception e){
            logger.error("" + e.getMessage());
            return CommunityUtil.getJSONString(1,"获取连接失败");
        }
    }
}
