package com.dangerye.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;

import java.util.List;
import java.util.stream.Collectors;

public class RestartingInstanceRouter implements Router {

    private final ReadyRestartInstances readyRestartInstances;
    private final URL url;

    public RestartingInstanceRouter(URL url) {
        this.readyRestartInstances = ReadyRestartInstances.getInstance();
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        return invokers.stream()
                .filter(invoker -> {
                    final String applicationName = invoker.getUrl().getParameter("remote.application");
                    final String host = invoker.getUrl().getIp();
                    System.out.println("applicationName: " + applicationName + " host: " + host);
                    return !readyRestartInstances.isRestartingInstance(applicationName, host);
                }).collect(Collectors.toList());
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean isForce() {
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
