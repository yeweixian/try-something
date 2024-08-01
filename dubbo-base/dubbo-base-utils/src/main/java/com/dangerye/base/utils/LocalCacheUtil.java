package com.dangerye.base.utils;

import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class LocalCacheUtil<T> implements Runnable {
    private final ConcurrentHashMap<String, Node<T>> cache;
    private final PriorityQueue<Node<T>> queue;

    private LocalCacheUtil(int initialCapacity) {
        cache = new ConcurrentHashMap<>(initialCapacity);
        queue = new PriorityQueue<>(initialCapacity);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this, 5, 5, TimeUnit.SECONDS);
    }

    public static <T> LocalCacheUtil<T> buildCache(int initialCapacity) {
        return new LocalCacheUtil<>(initialCapacity);
    }

    public List<T> findAll() {
        final long nowTime = System.currentTimeMillis();
        synchronized (this) {
            return cache.values().stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item.expireTime > nowTime)
                    .map(item -> item.value)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    public void cache(T value, long survivalTime) {
        final String key = UUID.randomUUID().toString();
        final long expireTime = System.currentTimeMillis() + survivalTime;
        final Node<T> newNode = new Node<>(key, value, expireTime);
        synchronized (this) {
            Node<T> old = cache.put(key, newNode);
            queue.add(newNode);
            if (old != null) {
                queue.remove(old);
            }
        }
    }

    @Override
    public void run() {
        final long nowTime = System.currentTimeMillis();
        while (true) {
            synchronized (this) {
                final Node<T> head = queue.peek();
                if (head == null || head.expireTime > nowTime) {
                    return;
                }
                cache.remove(head.key);
                queue.poll();
            }
        }
    }

    private static final class Node<T> implements Comparable<Node<T>> {
        private final String key;
        private final T value;
        private final long expireTime;

        private Node(String key, T value, long expireTime) {
            this.key = key;
            this.value = value;
            this.expireTime = expireTime;
        }

        @Override
        public int compareTo(Node<T> o) {
            long l = this.expireTime - o.expireTime;
            if (l > 0) {
                return 1;
            }
            if (l < 0) {
                return -1;
            }
            return 0;
        }
    }
}
