package com.dangerye.dubbo.consumer.bean;

import com.dangerye.dubbo.service.api.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class ConsumerComponent {
    @Reference(loadbalance = "")
    private HelloService helloService;

    public String sayHello(String name) {
        return helloService.sayHello(name);
    }
}
