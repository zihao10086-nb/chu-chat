package com.qiaochu.mallchat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

public class MyHeaderCollectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest)msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            Optional<String> token = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(query -> query.get("token"))
                    .map(CharSequence::toString);
            //如果token存在
            token.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            //移除后面拼接的所有参数
            request.setUri(urlBuilder.getPath().toString());
            //取用户ip
            String ip = request.headers().get("X-Real-IP");
            if (StringUtils.isBlank(ip)){
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip=address.getAddress().getHostAddress();
            }
            //存入用户ip到channel中
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            //处理器只需用一次
            ctx.pipeline().remove(this);
        }
        ctx.fireChannelRead(msg);
    }
}
