package com.dangerye.service;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface HelloService {
    String sayHello(String name);

    @Adaptive("helloService")
    String sayHello(URL url, String name);
}
