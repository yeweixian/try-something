package com.dangerye.base.utils;

import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.concurrent.ConcurrentHashMap;

public final class SingletonLoader<I> {
    private static final ConcurrentHashMap<Class<?>, SingletonLoader<?>> SINGLETON_LOADER_MAP = new ConcurrentHashMap<>();
    private final Class<I> clazz;
    private final ExtensionLoader<SingletonInstance> singletonInstanceExtensionLoader;

    private SingletonLoader(Class<I> clazz) {
        this.clazz = clazz;
        this.singletonInstanceExtensionLoader = ExtensionLoader.getExtensionLoader(SingletonInstance.class);
    }

    @SuppressWarnings("unchecked")
    public static <C> SingletonLoader<C> getSingletonLoader(Class<C> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        } else {
            return (SingletonLoader<C>) SINGLETON_LOADER_MAP.computeIfAbsent(clazz, SingletonLoader::new);
        }
    }
}
