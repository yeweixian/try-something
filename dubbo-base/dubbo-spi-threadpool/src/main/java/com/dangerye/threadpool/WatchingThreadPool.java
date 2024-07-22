package com.dangerye.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.threadpool.support.fixed.FixedThreadPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WatchingThreadPool extends FixedThreadPool implements Runnable {

    private static final double ALARM_PERCENT = 0.90;
    private final Map<URL, ThreadPoolExecutor> THREAD_POOLS = new ConcurrentHashMap<>();

    public WatchingThreadPool() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(this, 1, 3, TimeUnit.SECONDS);
    }

    @Override
    public Executor getExecutor(URL url) {
        final Executor executor = super.getExecutor(url);
        if (executor instanceof ThreadPoolExecutor) {
            THREAD_POOLS.put(url, (ThreadPoolExecutor) executor);
        }
        return executor;
    }

    @Override
    public void run() {
        for (Map.Entry<URL, ThreadPoolExecutor> entry : THREAD_POOLS.entrySet()) {
            final URL key = entry.getKey();
            final ThreadPoolExecutor value = entry.getValue();
            final int activeCount = value.getActiveCount();
            final int corePoolSize = value.getCorePoolSize();
            final double usedPercent = activeCount * 1.0 / (corePoolSize * 1.0);
            log.info("act status: [{}/{}:{}%]", activeCount, corePoolSize, usedPercent * 100);
            if (usedPercent > ALARM_PERCENT) {
                log.error("超出警戒线：host:{}, 当前使用率是：{}, url:{}", key.getIp(), usedPercent, key);
            }
        }
    }
}
