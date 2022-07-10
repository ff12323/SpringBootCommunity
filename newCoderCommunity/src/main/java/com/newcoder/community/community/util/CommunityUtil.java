package com.newcoder.community.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    /**
     * 生成随机字符串：
     * 比如生成激活码，上传文件或图片生成随机的名字。
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 使用MD5的方式对密码进行加密，注册时提交的密码时明文，保存时需要加密
     * MD5算法特定：1、只能加密，不能解密 比如：hello -(加密为,一一对应)> abc123def456；若黑客有一个简单密码的库
     * hello + salt随机字符串 --> absfdsf124322......; 更难破解
     * @param key
     * @return
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //生成6位随机验证码
    public static String generateVerifyCode(){
        String verifyCode = "";
        for(int i=0;i<6;i++){
            int j = RandomUtils.nextInt(0,10);
            verifyCode +=j;
        }
        return verifyCode;
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null) {
            for(String key:map.keySet()){
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    //有些时候可能没有业务数据或提示，或只有编号。 所以我们可以进行重载，便于调用
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","apple");
        map.put("age",123);
        System.out.println(getJSONString(0,"ok",map));
    }

}
