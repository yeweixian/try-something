package com.dangerye.scn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication7001 {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication7001.class, args);
    }
}
