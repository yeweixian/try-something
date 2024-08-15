package com.dangerye.base.utils;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class Loader<C> {
    private final Class<C> clazz;
    private volatile C instance;

    public Loader(Class<C> clazz) {
        this.clazz = clazz;
    }

    public C getInstance(Supplier<C> supplier) {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    return instance = supplier.get();
                } else {
                    return instance;
                }
            }
        } else {
            return instance;
        }
    }

    public C getInstance() {
        return getInstance(this::createInstance);
    }

    @SuppressWarnings("unchecked")
    private C createInstance() {
        try {
            final Method staticMethod = clazz.getDeclaredMethod("getInstance");
            staticMethod.setAccessible(true);
            return (C) staticMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
