package com.dangerye.router;

import com.dangerye.base.utils.Loader;
import com.dangerye.base.utils.ZookeeperClientInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public final class ReadyRestartInstances implements PathChildrenCacheListener {

    private static final String LISTEN_PATH = "/dubbo/restart/instances";
    private final Loader<Boolean> zkListenerLoader = new Loader<>(null);
    private final ExtensionLoader<ZookeeperClientInstance> zookeeperClientInstanceExtensionLoader
            = ExtensionLoader.getExtensionLoader(ZookeeperClientInstance.class);
    private volatile Set<String> nodeNameSet = Collections.emptySet();

    private ReadyRestartInstances() {
    }

    private static ReadyRestartInstances getInstance() {
        return new ReadyRestartInstances();
    }

    public void createZkListener() {
        final Boolean firstGet = zkListenerLoader.get();
        if (firstGet == null) {
            synchronized (zkListenerLoader) {
                final Boolean doubleGet = zkListenerLoader.get();
                if (doubleGet == null) {
                    zkListenerLoader.set(buildZkListener());
                }
            }
        }
    }

    private boolean buildZkListener() {
        try {
            final CuratorFramework zkClient = zookeeperClientInstanceExtensionLoader.getExtension("dubboRouter").getClient();
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
        final CuratorFramework zkClient = zookeeperClientInstanceExtensionLoader.getExtension("dubboRouter").getClient();
        zkClient.create()
                .creatingParentsIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    public void removeRestartingInstance(String applicationName, String host) throws Exception {
        final CuratorFramework zkClient = zookeeperClientInstanceExtensionLoader.getExtension("dubboRouter").getClient();
        zkClient.delete()
                .deletingChildrenIfNeeded()
                .forPath(LISTEN_PATH + "/" + buildNodeName(applicationName, host));
    }

    private String buildNodeName(String applicationName, String host) {
        return applicationName + "_" + host;
    }
}
