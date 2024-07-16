package com.dangerye.rpcclient.utils;

import com.alibaba.fastjson.JSON;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import com.dangerye.rpcclient.client.RpcClient;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RpcRemoteClientUtil implements InitializingBean, DisposableBean {

    private final ConcurrentHashMap<String, RpcClient> rpcClientMap = new ConcurrentHashMap<>();
    @Autowired
    private CuratorFramework zookeeperCuratorFramework;

    @SuppressWarnings("unchecked")
    public final <T> T createRemoteProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass},
                (proxy, method, args) -> {
                    final RpcClient rpcClient = loadBalancingRpcClient();
                    final RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setClassName(method.getDeclaringClass().getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setParameterTypes(method.getParameterTypes());
                    rpcRequest.setParameters(args);
                    final long beginTime = System.currentTimeMillis();
                    final String responseMsg = rpcClient.send(JSON.toJSONString(rpcRequest));
                    Thread.sleep(RandomUtils.nextInt(50, 250));
                    reportCallMsg(rpcClient.getService(), beginTime, System.currentTimeMillis());
                    final RpcResponse rpcResponse = JSON.parseObject(responseMsg, RpcResponse.class);
                    if (rpcResponse.getErrorMsg() != null) {
                        throw new RuntimeException(rpcResponse.getErrorMsg());
                    }
                    final Object result = rpcResponse.getResult();
                    return result == null ? null : JSON.parseObject(result.toString(), method.getReturnType());
                });
    }

    private RpcClient loadBalancingRpcClient() throws Exception {
        if (rpcClientMap.size() > 0) {
            long baseTime = Long.MAX_VALUE;
            RpcClient rpcClient = null;
            for (Map.Entry<String, RpcClient> entry : rpcClientMap.entrySet()) {
                final byte[] bytes = zookeeperCuratorFramework.getData().forPath("/" + entry.getKey());
                if (bytes == null) {
                    return entry.getValue();
                }
                final String nodeMsg = new String(bytes);
                if (StringUtils.isBlank(nodeMsg)) {
                    return entry.getValue();
                }
                final String[] split = nodeMsg.split("\\|");
                final long useTime = NumberUtils.toLong(split[1]);
                if (useTime < baseTime) {
                    baseTime = useTime;
                    rpcClient = entry.getValue();
                }
            }
            if (rpcClient != null) {
                return rpcClient;
            }
        }
        throw new RuntimeException("no provider service");
    }

    private void reportCallMsg(String service, long beginTime, long endTime) {
        try {
            final long useTime = endTime - beginTime;
            zookeeperCuratorFramework.setData()
                    .forPath("/" + service, (endTime + "|" + useTime).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            rpcClient.setService(service);
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
