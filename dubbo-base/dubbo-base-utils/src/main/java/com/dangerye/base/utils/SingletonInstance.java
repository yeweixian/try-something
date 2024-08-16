package com.dangerye.base.utils;

import org.apache.dubbo.common.extension.SPI;

@SPI
public interface SingletonInstance<I> {
    I getInstance();
}
