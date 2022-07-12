package com.newcoder.community.community.controller;

import com.newcoder.community.community.service.StaticDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class StaticDateController {

    @Autowired
    private StaticDateService staticDateService;

    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String data(){
        return "/site/admin/data";
    }

    @RequestMapping(path = "/dataUV",method = RequestMethod.POST)
    public String dataUV(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        long count = staticDateService.getUV(start,end);
        model.addAttribute("uv",count);
        return "forward:/data";
    }


}
