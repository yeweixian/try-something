package com.dangerye.autodeliver.services;

import org.springframework.stereotype.Component;

@Component
public class ResumeServiceFallback implements ResumeServiceFeignClient {
    @Override
    public Integer getStatusByUserId(Long userId) throws InterruptedException {
        return -1;
    }
}
