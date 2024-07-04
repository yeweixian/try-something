package com.dangerye.rpcserver.handle;

import com.dangerye.rpcapi.anno.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {

    private final Map<String, Object> serviceMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Map<String, Object> rpcServiceBeans = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (rpcServiceBeans.size() > 0) {
            for (Map.Entry<String, Object> entry : rpcServiceBeans.entrySet()) {
                final Object springBean = entry.getValue();
                if (springBean.getClass().getInterfaces().length == 0) {
                    throw new RuntimeException("rpc service must implement interface");
                }
                final String className = springBean.getClass().getInterfaces()[0].getName();
                serviceMap.put(className, springBean);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
    }
}
