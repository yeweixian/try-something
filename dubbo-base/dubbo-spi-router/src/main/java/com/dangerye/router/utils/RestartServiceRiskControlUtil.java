package com.dangerye.router.utils;

import com.dangerye.base.utils.Loader;
import com.dangerye.base.utils.SingletonLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public final class RestartServiceRiskControlUtil implements PathChildrenCacheListener {

    private static final String LISTEN_PATH = "/dubbo/restart/instances";
    private final Loader<Boolean> zkListenerLoader = new Loader<>();
    private final SingletonLoader<CuratorFramework> singletonLoader = SingletonLoader.getSingletonLoader(CuratorFramework.class);
    private volatile Set<String> nodeNameSet = Collections.emptySet();

    public void createZkListener() {
        zkListenerLoader.getInstance(this::buildZkListener);
    }

    private boolean buildZkListener() {
        try {
            final CuratorFramework zkClient = singletonLoader.getSingletonInstance("dubboRouterZkClient");
            final Stat stat = zkClient.checkExists().forPath(LISTEN_PATH);
            if (stat == null) {
                zkClient.create()
                        .creatingParentsIfNeeded()
                        .forPath(LISTEN_PATH);
            }
            final PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, LISTEN_PATH, true);
            pathChildrenCache.getListenable().addListener(this);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        final List<String> nodeNameList = curatorFramework.getChildren().forPath(LISTEN_PATH);
        System.out.println("nodes: " + nodeNameList);
        if (CollectionUtils.isEmpty(nodeNameList)) {
            nodeNameSet = Collections.emptySet();
        } else {
            nodeNameSet = new HashSet<>(nodeNameList);
        }
    }

    public boolean isRestartingInstance(String applicationName, String host) {
        return nodeNameSet.contains(buildNodeName(applicationName, host));
    }

    public void addRestartingInstance(String applicationName, String host) throws Exception {
        final CuratorFramework zkClient = singletonLoader.getSingletonInstance("dubboRouterZkClient");
        zkClient.create()
                .creatingParentsIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    public void removeRestartingInstance(String applicationName, String host) throws Exception {
        final CuratorFramework zkClient = singletonLoader.getSingletonInstance("dubboRouterZkClient");
        zkClient.delete()
                .deletingChildrenIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    private String buildNodeName(String applicationName, String host) {
        return applicationName + "_" + host;
    }
}
