package com.qiaochu.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.qiaochu.mallchat.common.user.dao.UserDao;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.service.UserService;
import com.qiaochu.mallchat.common.user.service.WXMsgService;
import com.qiaochu.mallchat.common.user.service.adapter.TextBuilder;
import com.qiaochu.mallchat.common.user.service.adapter.UserAdapter;
import com.qiaochu.mallchat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WXMsgServiceImpl implements WXMsgService {

    public static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    /**
     * openid 和 登录code的关系map
     */
    private static final ConcurrentHashMap<String,Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();
    @Value("${wx.mp.callback}")
    private String callback;
    @Resource
    private UserDao userDao;
    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private WxMpService wxMpService;
    @Resource
    private WebSocketService webSocketService;

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)) {
            return null;
        }
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        //用户已注册 并授权
        if (registered && authorized){
            //走登录成功的逻辑 通过code找到channel推送消息
            webSocketService.scanLoginSuccess(code,user.getId());
            return null;
        }
        if (!registered){
            User userSave = UserAdapter.buildUserSave(openId);
            userService.register(userSave);

        }
        //推送连接让用户授权
        WAIT_AUTHORIZE_MAP.put(openId,code);
        webSocketService.waitAuthorize(code);
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击登录:<a href=\"" + authorizeUrl + "\">登录</a> ", wxMpXmlMessage);

    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        //更新用户信息
        if (StrUtil.isNotBlank(userInfo.getHeadImgUrl())){
            fillUserInfo(user.getId(),userInfo);
        }
        //通过code 找到channel  进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        webSocketService.scanLoginSuccess(code,user.getId());
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorzeUser(uid, userInfo);
        userDao.updateById(user);
    }

    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {

        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("获取eventKey失败:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }
}
