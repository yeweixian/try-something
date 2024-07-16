package com.dangerye.rpcclient.utils;

import com.dangerye.rpcclient.client.RpcClient;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RpcRemoteClientUtil implements InitializingBean, DisposableBean {

    private final ConcurrentHashMap<String, RpcClient> rpcClientMap = new ConcurrentHashMap<>();
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

    private void connectProviderService(String service) {
        try {
            final String[] split = service.split(":");
            final RpcClient rpcClient = new RpcClient(split[0], NumberUtils.toInt(split[1]));
            rpcClientMap.putIfAbsent(service, rpcClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectProviderService(String service) {
        try (final RpcClient rpcClient = rpcClientMap.remove(service)) {
            rpcClient.tryClose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (rpcClientMap.size() > 0) {
            for (Map.Entry<String, RpcClient> entry : rpcClientMap.entrySet()) {
                final RpcClient rpcClient = entry.getValue();
                rpcClient.close();
            }
        }
    }
}
