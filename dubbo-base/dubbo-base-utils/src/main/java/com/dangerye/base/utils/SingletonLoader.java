package com.dangerye.base.utils;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SingletonLoader<I> {
    private static final ConcurrentHashMap<Class<?>, SingletonLoader<?>> SINGLETON_LOADER_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, SingletonInstance<?>> SINGLETON_INSTANCE_MAP = new ConcurrentHashMap<>();

    static {
        final ExtensionLoader<SingletonInstance> extensionLoader = ExtensionLoader.getExtensionLoader(SingletonInstance.class);
        final Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        if (CollectionUtils.isNotEmpty(supportedExtensions)) {
            for (String name : supportedExtensions) {
                SINGLETON_INSTANCE_MAP.computeIfAbsent(name, extensionLoader::getExtension);
            }
        }
    }

    private final Class<I> clazz;
    private final ConcurrentHashMap<String, Loader<I>> instanceLoaderMap;

    private SingletonLoader(Class<I> clazz) {
        this.clazz = clazz;
        this.instanceLoaderMap = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public static <C> SingletonLoader<C> getSingletonLoader(Class<C> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        } else {
            return (SingletonLoader<C>) SINGLETON_LOADER_MAP.computeIfAbsent(clazz, SingletonLoader::new);
        }
    }

    @SuppressWarnings("unchecked")
    public I getSingletonInstance(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new NullPointerException();
        }
        final SingletonInstance<?> singletonInstance = SINGLETON_INSTANCE_MAP.get(name);
        if (singletonInstance == null) {
            throw new IllegalStateException("No such SingletonInstance by name: " + name);
        }
        final Loader<I> instanceLoader = instanceLoaderMap.computeIfAbsent(name, mapKey -> new Loader<>());
        return instanceLoader.getInstance(() -> (I) singletonInstance.getInstance());
    }
}
