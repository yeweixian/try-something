package com.dangerye.dubbo.consumer;

import com.dangerye.dubbo.consumer.bean.ConsumerComponent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsumerMain {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) throws IOException {
        final AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        final ConsumerComponent consumerComponent = context.getBean(ConsumerComponent.class);
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(5, 15));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                EXECUTOR_SERVICE.execute(() -> {
                    final String s = consumerComponent.methodA();
                    System.out.println(s);
                });
            }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(5, 15));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                EXECUTOR_SERVICE.execute(() -> {
                    final String s = consumerComponent.methodB();
                    System.out.println(s);
                });
            }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(5, 15));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                EXECUTOR_SERVICE.execute(() -> {
                    final String s = consumerComponent.methodC();
                    System.out.println(s);
                });
            }
        }).start();
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
