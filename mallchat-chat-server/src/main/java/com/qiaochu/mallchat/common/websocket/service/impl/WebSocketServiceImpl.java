package com.qiaochu.mallchat.common.websocket.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiaochu.mallchat.common.common.event.UserOnlineEvent;
import com.qiaochu.mallchat.common.user.dao.UserDao;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.service.LoginService;
import com.qiaochu.mallchat.common.websocket.NettyUtil;
import com.qiaochu.mallchat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.qiaochu.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.qiaochu.mallchat.common.websocket.domain.vo.req.WSBaseReq;
import com.qiaochu.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.qiaochu.mallchat.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.qiaochu.mallchat.common.websocket.domain.vo.resp.WSLoginUrl;
import com.qiaochu.mallchat.common.websocket.service.WebSocketService;
import com.qiaochu.mallchat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务实现类
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * 管理所有在线用户连接（登录态/游客）   channel -> WSChannelExtraDTO 映射
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    private static final Duration DURATION = Duration.ofHours(1);
    private static final int MAXIMUM_SIZE = 10000;
    /**
     * 临时保存登录code和channel映射   code -> channel 映射
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();
    @Resource
    @Lazy
    private WxMpService wxMpService;
    @Resource
    private UserDao userDao;
    @Resource
    private LoginService loginService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机码
        Integer code = generateLoginCode(channel);
        //找微信生成带参数的二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        //把二维码推送给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        //todo 用户下线
    }

    @Override
    public void scanLoginSuccess(Integer code, Long id) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(id);
        //移除code
        WAIT_LOGIN_MAP.invalidate(code);
        //调用登录模块获取token
        String token = loginService.login(id);
        //用户登录
        loginSuccess(channel, user, token);
    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            User user = userDao.getById(validUid);
            loginSuccess(channel, user, token);
        } else {
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    private void loginSuccess(Channel channel, User user, String token) {
        //保存channel的对应uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        //推送成功消息
        sendMsg(channel, WebSocketAdapter.buildResp(user, token));
        //用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel,NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this,user));

    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }
}
