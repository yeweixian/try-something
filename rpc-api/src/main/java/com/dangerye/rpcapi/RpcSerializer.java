package com.dangerye.rpcapi;

public interface RpcSerializer {
    byte[] serialize(Object object) throws Exception;

    <R> R deserialize(Class<R> clazz, byte[] bytes) throws Exception;
}
