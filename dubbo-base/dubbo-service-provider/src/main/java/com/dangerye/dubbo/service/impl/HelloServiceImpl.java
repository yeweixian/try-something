package com.dangerye.dubbo.service.impl;

import com.dangerye.dubbo.service.api.HelloService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

@Service(retries = 0, timeout = 3000)
public class HelloServiceImpl implements HelloService {

    @Value("${dubbo.application.name}")
    private String appName;

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + ". I'm " + appName;
    }
}
