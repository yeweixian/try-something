package com.dangerye.base.utils;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public final class SingletonLoader<I> {
    private static final ConcurrentHashMap<Class<?>, SingletonLoader<?>> SINGLETON_LOADER_MAP = new ConcurrentHashMap<>();
    private final ExtensionLoader<SingletonInstance> extensionLoader;
    private final ConcurrentHashMap<String, Loader<I>> instanceLoaderMap;
    private final Class<I> clazz;

    private SingletonLoader(Class<I> clazz) {
        this.extensionLoader = ExtensionLoader.getExtensionLoader(SingletonInstance.class);
        this.instanceLoaderMap = new ConcurrentHashMap<>();
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public static <I> SingletonLoader<I> getSingletonLoader(Class<I> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        } else {
            return (SingletonLoader<I>) SINGLETON_LOADER_MAP.computeIfAbsent(clazz, SingletonLoader::new);
        }
    }

    public I getSingletonInstance(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new NullPointerException();
        }
        final SingletonInstance singletonInstance = extensionLoader.getExtension(name);
        if (singletonInstance == null) {
            throw new IllegalStateException("No such SingletonInstance by name: " + name);
        }
        final Loader<I> instanceLoader = instanceLoaderMap.computeIfAbsent(name, mapKey -> new Loader<>());
        return instanceLoader.getInstance(() -> clazz.cast(singletonInstance.getInstance()));
    }
}
