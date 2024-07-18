package com.dangerye.dubbo.consumer;

import com.dangerye.dubbo.consumer.bean.ConsumerComponent;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

public class ConsumerMain {
    public static void main(String[] args) throws IOException {
        final AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        final ConsumerComponent consumerComponent = context.getBean(ConsumerComponent.class);
        while (true) {
            System.in.read();
            final String result = consumerComponent.sayHello("world.");
            System.out.println("result: " + result);
        }
    }

    @Configuration
    @PropertySource("classpath:/dubbo-consumer.properties")
    @ComponentScan(basePackages = "com.dangerye.dubbo.consumer")
    @EnableDubbo // (scanBasePackages = "com.dangerye.dubbo.consumer")
    public static class ConsumerConfiguration {
    }
}
