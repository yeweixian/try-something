package com.dangerye.autodeliver;

import com.dangerye.scn.config.EnableResourceServerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
//@SpringCloudApplication = 以上集合
//@EnableResourceServerClient
public class AutoDeliverApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoDeliverApplication.class, args);
    }
}
