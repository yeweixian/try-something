package com.dangerye.rpcserver.handle;

import com.alibaba.fastjson.JSON;
import com.dangerye.rpcapi.RpcRequest;
import com.dangerye.rpcapi.RpcResponse;
import com.dangerye.rpcapi.anno.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
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
        final RpcRequest rpcRequest = JSON.parseObject(s, RpcRequest.class);
        final RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            rpcResponse.setResult(handle(rpcRequest));
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setErrorMsg(e.getMessage());
        }
        channelHandlerContext.writeAndFlush(JSON.toJSONString(rpcResponse));
    }

    private Object handle(RpcRequest rpcRequest) throws Exception {
        final Object springBean = serviceMap.get(rpcRequest.getClassName());
        if (springBean == null) {
            throw new RuntimeException("Not find bean. beanName: " + rpcRequest.getClassName());
        }
        final FastClass fastClass = FastClass.create(springBean.getClass());
        final FastMethod fastMethod = fastClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        return fastMethod.invoke(springBean, rpcRequest.getParameters());
    }
}
