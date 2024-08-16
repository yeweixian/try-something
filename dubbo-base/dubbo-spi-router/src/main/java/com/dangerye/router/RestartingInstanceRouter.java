package com.dangerye.router;

import com.dangerye.base.utils.SingletonLoader;
import com.dangerye.router.utils.RestartServiceRiskControlUtil;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;

import java.util.List;
import java.util.stream.Collectors;

public class RestartingInstanceRouter implements Router {

    private final RestartServiceRiskControlUtil riskControlUtil;
    private final URL url;

    public RestartingInstanceRouter(URL url) {
        final SingletonLoader<RestartServiceRiskControlUtil> singletonLoader = SingletonLoader.getSingletonLoader(RestartServiceRiskControlUtil.class);
        final RestartServiceRiskControlUtil riskControlUtil = singletonLoader.getSingletonInstance("restartServiceRiskControlUtil");
        riskControlUtil.createZkListener();
        this.riskControlUtil = riskControlUtil;
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
                    return !riskControlUtil.isRestartingInstance(applicationName, host);
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
