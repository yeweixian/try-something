package com.dangerye.autodeliver.controller;

import com.dangerye.autodeliver.services.ResumeServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autoDeliver")
public class AutoDeliverController {

    @Autowired
    private ResumeServiceFeignClient resumeServiceFeignClient;

    @RequestMapping("/checkStatus/{userId}")
    public Integer checkStatusByUserId(@PathVariable Long userId) throws InterruptedException {
        return resumeServiceFeignClient.getStatusByUserId(userId);
    }
}
