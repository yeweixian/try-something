package com.dangerye.router;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.dubbo.common.utils.Holder;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public final class ReadyRestartInstances implements PathChildrenCacheListener {

    private static final String LISTEN_PATH = "/dubbo/restart/instances";
    private static final Holder<ReadyRestartInstances> EXAMPLE = new Holder<>();
    private final CuratorFramework zkClient;
    private volatile Set<String> nodeNameSet = Collections.emptySet();

    private ReadyRestartInstances(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    public static ReadyRestartInstances getInstance() {
        final ReadyRestartInstances tryGet = EXAMPLE.get();
        if (tryGet == null) {
            synchronized (EXAMPLE) {
                final ReadyRestartInstances doubleTry = EXAMPLE.get();
                if (doubleTry == null) {
                    final ReadyRestartInstances instance = createInstance();
                    EXAMPLE.set(instance);
                    return instance;
                } else {
                    return doubleTry;
                }
            }
        } else {
            return tryGet;
        }
    }

    private static ReadyRestartInstances createInstance() {
        final CuratorFramework client = ZookeeperUtil.getClient();
        final ReadyRestartInstances instances = new ReadyRestartInstances(client);
        instances.createZkListener();
        return instances;
    }

    private void createZkListener() {
        try {
            final Stat stat = zkClient.checkExists().forPath(LISTEN_PATH);
            if (stat == null) {
                zkClient.create()
                        .creatingParentsIfNeeded()
                        .forPath(LISTEN_PATH);
            }
            final PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, LISTEN_PATH, true);
            pathChildrenCache.getListenable().addListener(this);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ReadyRestartInstances createZkListener error.", e);
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
        zkClient.create()
                .creatingParentsIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    public void removeRestartingInstance(String applicationName, String host) throws Exception {
        zkClient.delete()
                .deletingChildrenIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    private String buildNodeName(String applicationName, String host) {
        return applicationName + "_" + host;
    }
}
