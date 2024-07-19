package com.dangerye.spi;

import com.dangerye.service.HelloService;

import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        final ServiceLoader<HelloService> serviceLoader = ServiceLoader.load(HelloService.class);
        for (HelloService helloService : serviceLoader) {
            final String result = helloService.sayHello("main");
            System.out.println("result: " + result);
        }
    }
}
