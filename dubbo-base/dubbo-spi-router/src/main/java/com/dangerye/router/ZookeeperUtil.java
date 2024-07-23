package com.dangerye.router;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperUtil {
    private static final ZookeeperUtil UTIL;

    static {
        final ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retry)
                .namespace("dubboRouter")
                .build();
        curatorFramework.start();
        UTIL = new ZookeeperUtil(curatorFramework);
    }

    private final CuratorFramework curatorFramework;

    private ZookeeperUtil(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    public static CuratorFramework getClient() {
        return UTIL.curatorFramework;
    }
}
