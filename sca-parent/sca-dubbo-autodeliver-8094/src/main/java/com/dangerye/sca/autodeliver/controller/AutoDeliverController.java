package com.dangerye.sca.autodeliver.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.dangerye.sca.service.api.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autoDeliver")
public class AutoDeliverController {

    @Reference
    private HelloService helloService;

    @RequestMapping("/checkStatus/{userId}")
    @SentinelResource(value = "checkStatusByUserId", blockHandler = "fallbackCheckStatusByUserId")
    public Integer checkStatusByUserId(@PathVariable Long userId) throws InterruptedException {
        return helloService.getStatusByUserId(userId);
    }

    public Integer fallbackCheckStatusByUserId(Long userId, BlockException blockException) {
        return -1;
    }
}
