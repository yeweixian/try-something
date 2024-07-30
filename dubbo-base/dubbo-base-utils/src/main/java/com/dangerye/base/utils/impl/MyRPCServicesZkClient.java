package com.dangerye.base.utils.impl;

import com.dangerye.base.utils.SingletonLoader;
import com.dangerye.base.utils.ZookeeperClientInstance;
import com.dangerye.base.utils.ZookeeperUtil;
import org.apache.curator.framework.CuratorFramework;

public class MyRPCServicesZkClient implements ZookeeperClientInstance {
    @Override
    public CuratorFramework getClient() {
        final ZookeeperUtil zookeeperUtil = SingletonLoader.getInstance(ZookeeperUtil.class);
        return zookeeperUtil.getMyRPCServicesZkClient();
    }
}
