package com.qiaochu.mallchat.common.websocket.service;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyUtil {

    public static AttributeKey<String> TOKEN=AttributeKey.valueOf("token");
    public static <T> void setAttr(Channel channel, AttributeKey<T> key, T value){
        Attribute<T> attrKey = channel.attr(key);
        attrKey.set(value);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> key){
        Attribute<T> attrKey = channel.attr(key);
        return attrKey.get();
    }



}
