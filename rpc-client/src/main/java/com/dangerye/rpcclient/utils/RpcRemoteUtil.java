package com.dangerye.rpcclient.utils;

import com.alibaba.fastjson.JSON;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import com.dangerye.rpcapi.intf.TestService;
import com.dangerye.rpcapi.pojo.Model;
import com.dangerye.rpcclient.client.RpcClient;
import com.dangerye.rpcclient.client.RpcClientV2;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class RpcRemoteUtil {

    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        final TestService testService1 = RpcRemoteUtil.createRemoteProxy(TestService.class);
        final TestService testService2 = RpcRemoteUtil.createRemoteProxyV2(TestService.class);
        final List<Model> list = testService1.findAll();
        System.out.println(list);
        System.out.println("------------");
        for (int i = 0; i < 128; i++) {
            final int n = i;
            final long l = RandomUtils.nextLong(0, 5);
            executorService.execute(() -> {
                final Model model = testService2.findById(l == 0 ? null : l);
                System.out.println("--- thread i: " + n + " --- l: " + l + ", model: " + model);
            });
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createRemoteProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass},
                (proxy, method, args) -> {
                    final RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setClassName(method.getDeclaringClass().getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setParameterTypes(method.getParameterTypes());
                    rpcRequest.setParameters(args);
                    try (final RpcClient rpcClient = new RpcClient("127.0.0.1", 8989)) {
                        final Object responseMsg = rpcClient.send(JSON.toJSONString(rpcRequest));
                        final RpcResponse rpcResponse = JSON.parseObject(responseMsg.toString(), RpcResponse.class);
                        if (rpcResponse.getErrorMsg() != null) {
                            throw new RuntimeException(rpcResponse.getErrorMsg());
                        }
                        final Object result = rpcResponse.getResult();
                        return result == null ? null : JSON.parseObject(result.toString(), method.getReturnType());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public static <T> T createRemoteProxyV2(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass},
                (proxy, method, args) -> {
                    final RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setClassName(method.getDeclaringClass().getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setParameterTypes(method.getParameterTypes());
                    rpcRequest.setParameters(args);
                    try (final RpcClientV2 rpcClientV2 = new RpcClientV2("127.0.0.1", 8990)) {
                        final RpcResponse rpcResponse = rpcClientV2.send((rpcRequest));
                        if (rpcResponse.getErrorMsg() != null) {
                            throw new RuntimeException(rpcResponse.getErrorMsg());
                        }
                        final Object result = rpcResponse.getResult();
                        return result == null ? null : JSON.parseObject(result.toString(), method.getReturnType());
                    }
                });
    }
}
