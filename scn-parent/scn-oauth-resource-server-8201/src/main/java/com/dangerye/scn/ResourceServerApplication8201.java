package com.dangerye.scn;

import com.dangerye.scn.config.EnableResourceServerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServerClient
public class ResourceServerApplication8201 {
    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication8201.class, args);
    }
}
