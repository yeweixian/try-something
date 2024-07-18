package com.dangerye.dubbo.service.impl;

import com.dangerye.dubbo.service.api.HelloService;
import org.apache.dubbo.config.annotation.Service;

@Service(retries = 0, timeout = 3000)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
