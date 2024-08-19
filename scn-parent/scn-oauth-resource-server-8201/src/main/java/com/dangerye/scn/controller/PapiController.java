package com.dangerye.scn.controller;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/papi")
public class PapiController {

    @RequestMapping("/showMsg")
    public String showMsg(String access_token) {
        final int i = RandomUtils.nextInt(0, 10);
        return "Random Msg: " + i + " token: " + StringUtils.defaultIfBlank(access_token, "no token...");
    }
}
