package com.dangerye.dubbo.service.impl;

import com.dangerye.dubbo.service.api.BaseService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Service;

import java.util.concurrent.TimeUnit;

@Service(retries = 0, timeout = 3000)
public class BaseServiceImpl implements BaseService {
    @Override
    public String methodA() {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100, 300));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "methodA";
    }

    @Override
    public String methodB() {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100, 300));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "methodB";
    }

    @Override
    public String methodC() {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100, 300));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "methodC";
    }
}
