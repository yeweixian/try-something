package com.dangerye.base.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface ZookeeperClientInstance {
    CuratorFramework getClient();
}
