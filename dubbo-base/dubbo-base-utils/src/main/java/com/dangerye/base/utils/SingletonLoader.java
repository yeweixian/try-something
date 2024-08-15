package com.dangerye.base.utils;

import java.util.concurrent.ConcurrentHashMap;

public final class SingletonLoader<C> {
    private static final ConcurrentHashMap<Class<?>, SingletonLoader<?>> SINGLETON_LOADER_MAP = new ConcurrentHashMap<>();
    private final Class<C> clazz;

    private SingletonLoader(Class<C> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public static <C> SingletonLoader<C> getSingletonLoader(Class<C> clazz) {
        if (clazz == null) {
            return new SingletonLoader<>(null);
        } else {
            return (SingletonLoader<C>) SINGLETON_LOADER_MAP.computeIfAbsent(clazz, SingletonLoader::new);
        }
    }
}
