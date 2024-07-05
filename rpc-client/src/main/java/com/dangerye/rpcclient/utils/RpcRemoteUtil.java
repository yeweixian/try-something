package com.dangerye.rpcclient.utils;

import com.alibaba.fastjson.JSON;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import com.dangerye.rpcapi.intf.TestService;
import com.dangerye.rpcapi.pojo.Model;
import com.dangerye.rpcclient.client.RpcClient;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

public class RpcRemoteUtil {

    public static void main(String[] args) {
        final TestService testService = RpcRemoteUtil.createRemoteProxy(TestService.class);
        final List<Model> list = testService.findAll();
        System.out.println(list);
        System.out.println("------------");
        for (int i = 0; i < 100; i++) {
            final int n = i;
            final long l = RandomUtils.nextLong(0, 3);
            new Thread(() -> {
                final Model model = testService.findById(l == 0 ? null : l);
                System.out.println("--- thread i: " + n + " --- l: " + l + ", model: " + model);
            }).start();
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
}
