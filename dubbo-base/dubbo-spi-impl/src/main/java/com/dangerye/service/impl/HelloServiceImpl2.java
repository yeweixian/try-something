package com.dangerye.service.impl;

import com.dangerye.service.HelloService;
import org.apache.dubbo.common.URL;

public class HelloServiceImpl2 implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + ". I'm dubbo spi service2.";
    }

    @Override
    public String sayHello(URL url, String name) {
        return "Hello, " + name + ". I'm dubbo spi service2 url.";
    }
}
