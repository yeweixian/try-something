package com.dangerye.resume.controller;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    @RequestMapping("/status/{userId}")
    public Integer getStatusByUserId(@PathVariable Long userId) {
        final int i = RandomUtils.nextInt(0, 10);
        System.out.println("8082: " + i);
        return i;
    }
}
