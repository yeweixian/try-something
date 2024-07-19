package com.dangerye.service.impl;

import com.dangerye.service.HelloService;

public class HelloServiceImpl2 implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + ". I'm dubbo spi service2.";
    }
}
