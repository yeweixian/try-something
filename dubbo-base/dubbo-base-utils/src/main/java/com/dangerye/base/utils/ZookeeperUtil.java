package com.dangerye.base.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public final class ZookeeperUtil {
    private final Loader<CuratorFramework> myRPCServicesZkClientLoader = new Loader<>(null);
    private final Loader<CuratorFramework> dubboRouterZkClientLoader = new Loader<>(null);

    private ZookeeperUtil() {
    }

    private static ZookeeperUtil getInstance() {
        return new ZookeeperUtil();
    }

    public CuratorFramework getMyRPCServicesZkClient() {
        return myRPCServicesZkClientLoader.getInstance(this::createMyRPCServicesZkClient);
    }

    private CuratorFramework createMyRPCServicesZkClient() {
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

    public CuratorFramework getDubboRouterZkClient() {
        return dubboRouterZkClientLoader.getInstance(this::createDubboRouterZkClient);
    }

    private CuratorFramework createDubboRouterZkClient() {
        final ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retry)
                .namespace("dubboRouter")
                .build();
        zkClient.start();
        return zkClient;
    }
}
