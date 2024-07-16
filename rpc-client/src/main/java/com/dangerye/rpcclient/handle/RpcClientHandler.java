package com.dangerye.rpcclient.handle;

import com.alibaba.fastjson.JSON;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<String> {

    private final ConcurrentHashMap<String, RpcCode> rpcCodeMap = new ConcurrentHashMap<>();
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        final RpcResponse rpcResponse = JSON.parseObject(s, RpcResponse.class);
        final String requestId = rpcResponse.getRequestId();
        final RpcCode rpcCode = rpcCodeMap.get(requestId);
        rpcCodeMap.remove(requestId);
        rpcCode.setRpcResponse(rpcResponse);
        rpcCode.notifySelf();
    }

    public void send(RpcRequest rpcRequest) throws Exception {
        final RpcCode rpcCode = new RpcCode();
        rpcCodeMap.put(rpcRequest.getRequestId(), rpcCode);
        final String msg = JSON.toJSONString(rpcRequest);
        channel.writeAndFlush(msg);
        rpcCode.waitSelf();
    }

    @Data
    public static final class RpcCode {
        private RpcResponse rpcResponse;

        public synchronized void waitSelf() throws InterruptedException {
            this.wait();
        }

        public synchronized void notifySelf() {
            this.notify();
        }
    }
}
