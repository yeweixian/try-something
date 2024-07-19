package com.dangerye.service.impl;

import com.dangerye.service.HelloService;
import org.apache.dubbo.common.URL;

public class HelloServiceImpl1 implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + ". I'm dubbo spi service1.";
    }

    @Override
    public String sayHello(URL url, String name) {
        return "Hello, " + name + ". I'm dubbo spi service1 url.";
    }
}
