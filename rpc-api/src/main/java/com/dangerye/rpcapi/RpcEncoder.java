package com.dangerye.rpcapi;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<Object> {

    private final Class<?> clazz;
    private final RpcSerializer rpcSerializer;

    public RpcEncoder(Class<?> clazz, RpcSerializer rpcSerializer) {
        this.clazz = clazz;
        this.rpcSerializer = rpcSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz != null && clazz.isInstance(o)) {
            byte[] serialize = rpcSerializer.serialize(o);
            byteBuf.writeInt(serialize.length);
            byteBuf.writeBytes(serialize);
        }
    }
}
