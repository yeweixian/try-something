package com.dangerye.rpcclient.client;

import com.dangerye.rpcapi.RpcDecoder;
import com.dangerye.rpcapi.RpcEncoder;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import com.dangerye.rpcapi.impl.JsonRpcSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;
import java.io.IOException;

public class RpcClientV2 implements Closeable {

    private final String ip;
    private final int port;
    private NioEventLoopGroup group;
    private Channel channel;
    private RpcResponse rpcResponse;

    public RpcClientV2(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public RpcResponse send(RpcRequest rpcRequest) throws Exception {
        group = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JsonRpcSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JsonRpcSerializer()));
                        pipeline.addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse s) throws Exception {
                                rpcResponse = s;
                                notifySelf();
                            }
                        });
                    }
                });
        final ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        channel = channelFuture.channel();
        channel.writeAndFlush(rpcRequest);
        waitSelf();
        return rpcResponse;
    }

    public synchronized void waitSelf() throws InterruptedException {
        this.wait();
    }

    public synchronized void notifySelf() {
        this.notify();
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
            System.out.println("--- channel close ---");
        }
        if (group != null) {
            group.shutdownGracefully();
            System.out.println("--- group close ---");
        }
    }
}
