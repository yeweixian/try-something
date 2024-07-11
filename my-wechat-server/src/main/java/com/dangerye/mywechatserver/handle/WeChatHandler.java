package com.dangerye.mywechatserver.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ChannelHandler.Sharable
public class WeChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public final List<Channel> channelList = new ArrayList<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        channelList.add(channel);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        channelList.remove(channel);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        final Channel channel = ctx.channel();
        channelList.remove(channel);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        final String msg = textWebSocketFrame.text();
        final Channel channel = channelHandlerContext.channel();
        for (Channel item : channelList) {
            if (channel != item) {
                item.writeAndFlush(new TextWebSocketFrame(msg));
            }
        }
    }
}
