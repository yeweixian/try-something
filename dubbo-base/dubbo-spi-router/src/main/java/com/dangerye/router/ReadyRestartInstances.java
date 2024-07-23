package com.dangerye.router;

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
public class ReadyRestartInstances implements PathChildrenCacheListener {

    private static final String LISTEN_PATH = "/dubbo/restart/instances";
    private static final CuratorFramework ZKCLIENT;
    private static final ReadyRestartInstances INSTANCES;

    static {
        ZKCLIENT = ZookeeperUtil.getClient();
        INSTANCES = new ReadyRestartInstances();
        try {
            final Stat stat = ZKCLIENT.checkExists().forPath(LISTEN_PATH);
            if (stat == null) {
                ZKCLIENT.create()
                        .creatingParentsIfNeeded()
                        .forPath(LISTEN_PATH);
            }
            final PathChildrenCache pathChildrenCache = new PathChildrenCache(ZKCLIENT, LISTEN_PATH, true);
            pathChildrenCache.getListenable().addListener(INSTANCES);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ReadyRestartInstances init error.", e);
        }
    }

    private volatile Set<String> nodeNameSet = Collections.emptySet();

    private ReadyRestartInstances() {
    }

    public static ReadyRestartInstances getInstance() {
        return INSTANCES;
    }

    private String buildNodeName(String applicationName, String host) {
        return applicationName + "_" + host;
    }

    public void addRestartingInstance(String applicationName, String host) throws Exception {
        ZKCLIENT.create()
                .creatingParentsIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    public void removeRestartingInstance(String applicationName, String host) throws Exception {
        ZKCLIENT.delete()
                .deletingChildrenIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    public boolean isRestartingInstance(String applicationName, String host) {
        return nodeNameSet.contains(buildNodeName(applicationName, host));
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
}
