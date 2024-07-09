package com.dangerye.rpcapi;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private final Class<?> clazz;
    private final RpcSerializer rpcSerializer;

    public RpcDecoder(Class<?> clazz, RpcSerializer rpcSerializer) {
        this.clazz = clazz;
        this.rpcSerializer = rpcSerializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = 0;
        while (length == 0) {
            length = byteBuf.readInt();
        }
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object object = rpcSerializer.deserialize(clazz, bytes);
        list.add(object);
    }
}
