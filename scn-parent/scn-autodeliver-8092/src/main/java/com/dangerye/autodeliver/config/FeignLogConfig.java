package com.dangerye.autodeliver.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLogConfig {
    @Bean
    public Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }
}
