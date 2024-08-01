package com.dangerye.dubbo.consumer.bean;

import com.dangerye.dubbo.service.api.BaseService;
import com.dangerye.dubbo.service.api.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class ConsumerComponent {
    @Reference(loadbalance = "onlyFirst")
    private HelloService helloService;
    @Reference(loadbalance = "onlyFirst")
    private BaseService baseService;

    public String sayHello(String name) {
        return helloService.sayHello(name);
    }

    public String methodA() {
        return baseService.methodA();
    }

    public String methodB() {
        return baseService.methodB();
    }

    public String methodC() {
        return baseService.methodC();
    }
}
