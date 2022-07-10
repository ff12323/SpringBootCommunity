package com.newcoder.community.community;


import com.newcoder.community.community.util.CommunityUtil;
import org.junit.Test;

public class UtilTests {

    @Test
    public void verifyCodeTest(){
        for(int i=0;i<5;i++){
            System.out.println(CommunityUtil.generateVerifyCode());
        }
    }

    @Test
    public void md5Test(){
        System.out.println(CommunityUtil.md5("123" + "12345"));
    }



}
