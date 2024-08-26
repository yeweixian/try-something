package com.dangerye.sca.autodeliver.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "resume-application", path = "/resume")
// @RequestMapping("/resume")
public interface ResumeServiceFeignClient {
    @RequestMapping("/status/{userId}")
    Integer getStatusByUserId(@PathVariable("userId") Long userId) throws InterruptedException;
}
