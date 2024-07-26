package com.dangerye.router;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.dubbo.common.utils.Holder;

public final class ZookeeperUtil {

    private static final Holder<CuratorFramework> ZKCLIENT = new Holder<>();

    private ZookeeperUtil() {
    }

    public static CuratorFramework getClient() {
        final CuratorFramework tryGet = ZKCLIENT.get();
        if (tryGet == null) {
            synchronized (ZKCLIENT) {
                final CuratorFramework doubleTry = ZKCLIENT.get();
                if (doubleTry == null) {
                    final CuratorFramework instance = createClient();
                    ZKCLIENT.set(instance);
                    return instance;
                } else {
                    return doubleTry;
                }
            }
        } else {
            return tryGet;
        }
    }

    private static CuratorFramework createClient() {
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
