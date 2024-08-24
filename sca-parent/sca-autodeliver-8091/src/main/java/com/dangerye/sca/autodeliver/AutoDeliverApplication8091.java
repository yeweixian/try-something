package com.dangerye.sca.autodeliver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AutoDeliverApplication8091 {
    public static void main(String[] args) {
        SpringApplication.run(AutoDeliverApplication8091.class, args);
    }
}
