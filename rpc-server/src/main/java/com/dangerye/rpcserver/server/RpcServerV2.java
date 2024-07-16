package com.dangerye.rpcserver.server;

import com.dangerye.rpcapi.RpcDecoder;
import com.dangerye.rpcapi.RpcEncoder;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import com.dangerye.rpcapi.impl.JsonRpcSerializer;
import com.dangerye.rpcserver.config.RpcConfigV2;
import com.dangerye.rpcserver.handle.RpcServerHandlerV2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(RpcConfigV2.class)
public class RpcServerV2 implements InitializingBean, DisposableBean, Runnable {

    @Autowired
    private RpcConfigV2 rpcConfigV2;
    @Autowired
    private RpcServerHandlerV2 rpcServerHandlerV2;
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
                            pipeline.addLast(new RpcEncoder(RpcResponse.class, new JsonRpcSerializer()));
                            pipeline.addLast(new RpcDecoder(RpcRequest.class, new JsonRpcSerializer()));
                            pipeline.addLast(rpcServerHandlerV2);
                        }
                    });
            int port = rpcConfigV2.getPort() != null ? rpcConfigV2.getPort() : 9090;
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
        // new Thread(this).start();
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
