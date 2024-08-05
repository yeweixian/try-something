package com.dangerye.resume.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    @RequestMapping("/status/{userId}")
    public Integer getStatusByUserId(@PathVariable Long userId) {
        return 0;
    }
}
