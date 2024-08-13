package com.dangerye.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StreamConsumerApplication8111 {
    public static void main(String[] args) {
        SpringApplication.run(StreamConsumerApplication8111.class, args);
    }
}
