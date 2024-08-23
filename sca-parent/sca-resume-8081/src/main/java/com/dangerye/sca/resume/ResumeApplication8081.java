package com.dangerye.sca.resume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ResumeApplication8081 {
    public static void main(String[] args) {
        SpringApplication.run(ResumeApplication8081.class, args);
    }
}
