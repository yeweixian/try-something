package com.dangerye.base.utils.impl;

import com.dangerye.base.utils.SingletonInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class MyRPCServicesZkClient implements SingletonInstance<CuratorFramework> {
    @Override
    public CuratorFramework getInstance() {
        final ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retry)
                .namespace("myRPCServices")
                .build();
        curatorFramework.start();
        return curatorFramework;
    }
}
