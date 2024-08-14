package com.dangerye.scn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin2.server.internal.EnableZipkinServer;

@SpringBootApplication
@EnableZipkinServer
public class ZipkinApplication9411 {
    public static void main(String[] args) {
        SpringApplication.run(ZipkinApplication9411.class, args);
    }
}
