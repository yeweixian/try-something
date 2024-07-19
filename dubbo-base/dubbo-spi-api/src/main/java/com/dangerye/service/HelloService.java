package com.dangerye.service;

import org.apache.dubbo.common.extension.SPI;

@SPI
public interface HelloService {
    String sayHello(String name);
}
