package com.dangerye.loadbalance;

import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

public class OnlyFirstLoadBalance implements LoadBalance {
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        final int size = invokers.size();
        final int i = RandomUtils.nextInt(0, size);
        return invokers.get(i);
    }
}
