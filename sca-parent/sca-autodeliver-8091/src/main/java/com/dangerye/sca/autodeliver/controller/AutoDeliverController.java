package com.dangerye.sca.autodeliver.controller;

import com.dangerye.sca.autodeliver.service.ResumeServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autoDeliver")
public class AutoDeliverController {

    @Qualifier("com.dangerye.sca.autodeliver.service.ResumeServiceFeignClient")
    @Autowired
    private ResumeServiceFeignClient resumeServiceFeignClient;

    @RequestMapping("/checkStatus/{userId}")
    public Integer checkStatusByUserId(@PathVariable Long userId) throws InterruptedException {
        return resumeServiceFeignClient.getStatusByUserId(userId);
    }
}
