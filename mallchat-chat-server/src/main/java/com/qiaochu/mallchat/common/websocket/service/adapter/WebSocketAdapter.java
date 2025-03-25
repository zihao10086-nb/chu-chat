package com.qiaochu.mallchat.common.websocket.service.adapter;

import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.qiaochu.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.qiaochu.mallchat.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.qiaochu.mallchat.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    public static WSBaseResp<?> buildResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess build = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .uid(user.getId())
                .name(user.getName())
                .token(token)
                .build();
        resp.setData(build);
        return resp;
    }

    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }
}
