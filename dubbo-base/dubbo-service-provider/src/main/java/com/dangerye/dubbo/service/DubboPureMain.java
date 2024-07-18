package com.dangerye.dubbo.service;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

public class DubboPureMain {
    public static void main(String[] args) throws IOException {
        final AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(ProviderConfiguration.class);
        context.start();
        System.in.read();
    }

    @Configuration
    @PropertySource("classpath:/dubbo-provider.properties")
    @ComponentScan(basePackages = "com.dangerye.dubbo.service")
    @EnableDubbo(scanBasePackages = "com.dangerye.dubbo.service.impl")
    public static class ProviderConfiguration {
        @Bean
        public RegistryConfig registryConfig() {
            final RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress("zookeeper://127.0.0.1:2181");
            return registryConfig;
        }
    }
}
