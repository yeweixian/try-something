package com.dangerye.sca.resume.service.impl;

import com.dangerye.sca.service.api.HelloService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Service;

import java.util.concurrent.TimeUnit;

@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public Integer getStatusByUserId(Long userId) {
        try {
            final int i = RandomUtils.nextInt(0, 10);
            TimeUnit.SECONDS.sleep(i);
            System.out.println("8083: " + i);
            return i;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
