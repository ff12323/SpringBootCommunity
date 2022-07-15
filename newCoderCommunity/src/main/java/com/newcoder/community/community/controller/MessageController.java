package com.newcoder.community.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.community.entity.Event;
import com.newcoder.community.community.entity.Message;
import com.newcoder.community.community.entity.Page;
import com.newcoder.community.community.entity.User;
import com.newcoder.community.community.service.MessageService;
import com.newcoder.community.community.service.UserService;
import com.newcoder.community.community.util.CommunityConstant;
import com.newcoder.community.community.util.CommunityUtil;
import com.newcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;



    //系统通知页面
    @RequestMapping(path = "/letter/notice",method = RequestMethod.GET)
    public String getLetterNotice(Model model){

        User user = hostHolder.getUser();


        //系统消息：评论
        int umCount = messageService.findSystemMsgUnreadCount(user.getId(),TOPIC_COMMENT);
        Map<String,Object> commentMsg = new HashMap<>();
        commentMsg.put("umCount",umCount);
        Message cMes = messageService.getNewestUnreadMsg(user.getId(),TOPIC_COMMENT);
        Event event = JSONObject.parseObject(
                HtmlUtils.htmlUnescape(cMes.getContent()),Event.class);
        commentMsg.put("user",userService.findUserById(event.getUserId()));
        commentMsg.put("createTime",cMes.getCreateTime());
        model.addAttribute("commentMsg",commentMsg);



        return "/site/notice";
    }



    //私信列表
    //页面向服务器传参，会被自动做转换。比如向page的参数，自动转为page对象。为什么可以？Spring MVC底层内置了很多参数转换器。
    //你传的参数，它自动做出判断，调用了参数。但是加入你有一个全新的类型，特殊的类型需要特殊的处理。我们可以自定义一个转换器。用@DataBinder注册上
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> coList = messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversations != null){
            for(Message message:coList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //查询整个页面的未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }


    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Model model,Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message message:letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        //私信目标
        model.addAttribute("target",getLetterTarget(conversationId));

        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }


    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findByUsername(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }

        User user = hostHolder.getUser();
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_"+ message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_"+ message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }


    /**
     * 根据conversationId,返回私信会话的目标用户（与当前用户相反）
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int d0 = Integer.valueOf(ids[0]);
        int d1 = Integer.valueOf(ids[1]);

        if(hostHolder.getUser().getId() == d0){
            return userService.findUserById(d1);
        }else {
            return hostHolder.getUser();
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(ids != null){
            for(Message message: letterList){
                //只有该消息的发送者不为当前用户，且消息的状态为未读 才需要将其加入列表。
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
}
