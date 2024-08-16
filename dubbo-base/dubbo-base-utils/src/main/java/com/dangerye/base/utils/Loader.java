package com.dangerye.base.utils;

import java.util.function.Supplier;

public final class Loader<C> {
    private volatile C instance;

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
}
