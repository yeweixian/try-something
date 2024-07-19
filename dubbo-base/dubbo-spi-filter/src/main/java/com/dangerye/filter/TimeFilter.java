package com.dangerye.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

@Activate(group = {CommonConstants.CONSUMER})
public class TimeFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        final long startTime = System.currentTimeMillis();
        try {
            return invoker.invoke(invocation);
        } finally {
            System.out.println("rpc call time: " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }
}
