package com.qiaochu.mallchat.common.websocket.service;

import io.netty.channel.Channel;

/**
 * WebSocket服务接口
 */
public interface WebSocketService {
    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long id);

    void waitAuthorize(Integer code);
}
