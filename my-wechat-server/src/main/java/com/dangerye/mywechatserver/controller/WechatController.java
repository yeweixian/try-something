package com.dangerye.mywechatserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WechatController {

    @RequestMapping("/")
    public String chat() {
        return "chat";
    }
}
