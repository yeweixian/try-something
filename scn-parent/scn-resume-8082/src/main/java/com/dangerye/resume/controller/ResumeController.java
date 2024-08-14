package com.dangerye.resume.controller;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/resume")
@RefreshScope
public class ResumeController {

    @Value("${server.config.test}")
    private String serverConfigTest;

    @RequestMapping("/showConfigMsg")
    public String showConfigTest() {
        return serverConfigTest;
    }

    @RequestMapping("/status/{userId}")
    public Integer getStatusByUserId(@PathVariable Long userId) throws InterruptedException {
        final int i = RandomUtils.nextInt(0, 10);
        TimeUnit.SECONDS.sleep(i);
        System.out.println("8082: " + i);
        return i;
    }
}
