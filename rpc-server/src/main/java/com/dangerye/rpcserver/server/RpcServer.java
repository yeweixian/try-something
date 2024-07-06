package com.dangerye.rpcserver.server;

import com.dangerye.rpcserver.config.RpcConfig;
import com.dangerye.rpcserver.handle.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(RpcConfig.class)
public class RpcServer implements InitializingBean, DisposableBean, Runnable {

    @Autowired
    private RpcConfig rpcConfig;
    @Autowired
    private RpcServerHandler rpcServerHandler;
    private NioEventLoopGroup masterGroup;
    private NioEventLoopGroup workerGroup;

    @Override
    public void run() {
        try {
            masterGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(masterGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            final ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(rpcServerHandler);
                        }
                    });
            int port = rpcConfig.getPort() != null ? rpcConfig.getPort() : 9090;
            final ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("------ rpc server start success ------");
            System.out.println("------ rpc server port: " + port + " ------");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            if (masterGroup != null) {
                masterGroup.shutdownGracefully();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this).start();
    }

    @Override
    public void destroy() throws Exception {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (masterGroup != null) {
            masterGroup.shutdownGracefully();
        }
    }
}
