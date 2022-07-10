package com.newcoder.community.community.service;

import com.newcoder.community.community.dao.MessageMapper;
import com.newcoder.community.community.util.SensitiveFilter;
import com.newcoder.community.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit){
        List<Message> list = messageMapper.selectConversations(userId,offset,limit);
        return list;
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }


    public List<Message> findLetters(String conversationId,int offset, int limit){
        List<Message> list = messageMapper.selectLetters(conversationId,offset,limit);
        return list;
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }


    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }


    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    //设置消息为已读
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids, 1);
    }

}
