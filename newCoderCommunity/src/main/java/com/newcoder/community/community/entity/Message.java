package com.newcoder.community.community.entity;

import java.util.Date;

public class Message {
    private int id;

    /**
     * 消息发布人的ID
     */
    private int fromId;

    /**
     * 消息接收人的ID
     */
    private int toId;

    /**
     * 会话ID
     * <ul>
     *     <li>1、2个用户发消息（存储：发送人ID_接收人ID）</li>
     *     <li>2、后台系统_用户消息 （存储：消息主题）</li>
     * </ul>
     */
    private String conversationId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息状态
     */
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", conversationId='" + conversationId + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
