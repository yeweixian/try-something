package com.dangerye.rpcclient.utils;

import com.alibaba.fastjson.JSON;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import com.dangerye.rpcapi.intf.TestService;
import com.dangerye.rpcapi.pojo.Model;
import com.dangerye.rpcclient.client.RpcClient;

import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcRemoteUtil {

    public static void main(String[] args) {
        final TestService testService = RpcRemoteUtil.createRemoteProxy(TestService.class);
        final Model model = testService.findById(null);
        System.out.println(model);
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
