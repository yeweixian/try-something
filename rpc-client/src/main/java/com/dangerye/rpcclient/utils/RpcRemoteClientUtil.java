package com.dangerye.rpcclient.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RpcRemoteClientUtil implements InitializingBean {

    @Autowired
    private CuratorFramework zookeeperCuratorFramework;

    @Override
    public void afterPropertiesSet() throws Exception {
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(zookeeperCuratorFramework, "/", true);
        pathChildrenCache.getListenable()
                .addListener((cf, ce) -> {
                    final PathChildrenCacheEvent.Type type = ce.getType();
                    System.out.println("event type: " + type);
                    if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(type)) {
                        final String path = ce.getData().getPath();
                        System.out.println("act node: " + path);
                        connectProviderService(path.substring(1));
                    } else if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(type)) {
                        final String path = ce.getData().getPath();
                        System.out.println("act node: " + path);
                        disconnectProviderService(path.substring(1));
                    }
                });
        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
