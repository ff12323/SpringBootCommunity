package com.newcoder.community.community.entity;


import java.util.HashMap;
import java.util.Map;

/**
 * 通知（用户通知、系统通知）的主体内容与关键消息！
 */
public class Event {

    /**
     * 事件的主体，Kafka
     */
    private String topic;

    /**
     * 事件发出者Id
     */
    private int userId;

    /**
     * 事件发生在哪个实体上：类型
     */
    private int entityType;

    /**
     * 事件发生在哪个实体上：Id
     */
    private int entityId;


    /**
     * 事件发生在哪个实体上：实体作者
     */
    private int entityUserId;

    /**
     * 事件的更多数据
     */
    private Map<String,Object> data = new HashMap<>();


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(String key,Object data) {
        this.data.put(key,data);
    }
}
