package com.dangerye.sca.resume.controller;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/resume")
public class ResumeController {
    @RequestMapping("/status/{userId}")
    public Integer getStatusByUserId(@PathVariable Long userId) throws InterruptedException {
        final int i = RandomUtils.nextInt(0, 10);
        TimeUnit.SECONDS.sleep(i);
        System.out.println("8081: " + i);
        return i;
    }
}
