package com.dangerye.mywechatserver.server;

import com.dangerye.mywechatserver.config.WeChatConfig;
import com.dangerye.mywechatserver.handle.WeChatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(WeChatConfig.class)
public class WeChatServer implements InitializingBean, DisposableBean, Runnable {

    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private WeChatHandler weChatHandler;
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
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            final ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            pipeline.addLast(new WebSocketServerProtocolHandler(weChatConfig.getPath()));
                            pipeline.addLast(weChatHandler);
                        }
                    });
            int port = weChatConfig.getPort() != null ? weChatConfig.getPort() : 8081;
            final ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("------ weChat server start success ------");
            System.out.println("------ weChat server port: " + port + " ------");
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
