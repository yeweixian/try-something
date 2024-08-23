package com.dangerye.scn.controller;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/rapi")
public class RapiController {

    @RequestMapping("/showMsg")
    public String showMsg(String access_token) {
        // jwt clientIp 获取逻辑...
        final Object details = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getDetails)
                .orElse(null);
        final int i = RandomUtils.nextInt(0, 10);
        return "Random Msg: " + i + " token: " + StringUtils.defaultIfBlank(access_token, "no token...");
    }
}
