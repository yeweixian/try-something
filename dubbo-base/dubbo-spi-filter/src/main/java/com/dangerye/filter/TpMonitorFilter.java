package com.dangerye.filter;

import com.dangerye.base.utils.Loader;
import com.dangerye.base.utils.LocalCacheUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Activate(group = {CommonConstants.CONSUMER})
public class TpMonitorFilter implements Filter, Runnable {
    private static final LocalCacheUtil<Transfer> TP_MONITOR_CACHE = LocalCacheUtil.buildCache(20000);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private static final Loader<Boolean> SCHEDULED_LOADER = new Loader<>(null);

    public TpMonitorFilter() {
        final Boolean first = SCHEDULED_LOADER.get();
        if (first == null) {
            synchronized (SCHEDULED_LOADER) {
                final Boolean doubleCheck = SCHEDULED_LOADER.get();
                if (doubleCheck == null) {
                    SCHEDULED_LOADER.set(buildScheduled());
                }
            }
        }
    }

    private Boolean buildScheduled() {
        SCHEDULED_EXECUTOR.scheduleWithFixedDelay(this, 5, 5, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        final long beginTime = System.currentTimeMillis();
        final String serviceName = invocation.getServiceName();
        final String methodName = invocation.getMethodName();
        try {
            return invoker.invoke(invocation);
        } finally {
            final long transferTime = System.currentTimeMillis() - beginTime;
            final Transfer transfer = new Transfer(serviceName + "#" + methodName, transferTime);
            TP_MONITOR_CACHE.cache(transfer, 60000);
        }
    }

    @Override
    public void run() {
        System.out.println("------------ line ------------");
        final List<Transfer> transferList = TP_MONITOR_CACHE.findAll();
        if (transferList.isEmpty()) {
            System.out.println("consumer tp monitor data is empty...");
            return;
        }
        System.out.println("transferList size: " + transferList.size());
        final Map<String, List<Transfer>> groupMap = transferList.stream()
                .collect(Collectors.groupingBy(item -> item.transferName));
        for (Map.Entry<String, List<Transfer>> entry : groupMap.entrySet()) {
            final String key = entry.getKey();
            final List<Transfer> value = entry.getValue();
            if (value == null || value.isEmpty()) {
                System.out.println("TP_MONITOR: " + key + "\t\t size: null,\t tp90: null,\t tp99: null");
                continue;
            }
            final int size = value.size();
            value.sort(Comparator.comparing(item -> item.transferTime));
            final int tp90 = size * 90 / 100;
            final int tp99 = size * 99 / 100;
            final long transferTimeByTp90 = value.get(tp90).transferTime;
            final long transferTimeByTp99 = value.get(tp99).transferTime;
            System.out.println("TP_MONITOR: " + key + "\t\t size: " + size + ",\t tp90: " + transferTimeByTp90 + "ms,\t tp99: " + transferTimeByTp99 + "ms");
        }
        System.out.println("------------------------------");
    }

    private static final class Transfer {
        private final String transferName;
        private final long transferTime;

        private Transfer(String transferName, long transferTime) {
            this.transferName = transferName;
            this.transferTime = transferTime;
        }
    }
}
