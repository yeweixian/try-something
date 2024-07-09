package com.dangerye.rpcapi.impl;

import com.alibaba.fastjson.JSON;
import com.dangerye.rpcapi.RpcSerializer;

public class JsonRpcSerializer implements RpcSerializer {
    @Override
    public byte[] serialize(Object object) throws Exception {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <R> R deserialize(Class<R> clazz, byte[] bytes) throws Exception {
        return JSON.parseObject(bytes, clazz);
    }
}
