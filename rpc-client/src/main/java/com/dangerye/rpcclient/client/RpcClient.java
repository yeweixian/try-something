package com.dangerye.rpcclient.client;

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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.Closeable;
import java.io.IOException;

public class RpcClient implements Closeable {

    private final String ip;
    private final int port;
    private String service;
    private NioEventLoopGroup group;
    private Channel channel;
    private String responseMsg;

    public RpcClient(String ip, int port) throws Exception {
        this.ip = ip;
        this.port = port;
        connectionServer();
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    private void connectionServer() throws Exception {
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
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                                responseMsg = s;
                                notifySelf();
                            }
                        });
                    }
                });
        final ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        channel = channelFuture.channel();
    }

    public String send(String msg) throws Exception {
        channel.writeAndFlush(msg);
        waitSelf();
        return responseMsg;
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

    public void tryClose() {
        System.out.println("client close. service: " + ip + ":" + port);
    }
}
