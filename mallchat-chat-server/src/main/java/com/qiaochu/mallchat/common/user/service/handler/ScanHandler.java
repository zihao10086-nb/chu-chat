package com.qiaochu.mallchat.common.user.service.handler;

import com.qiaochu.mallchat.common.user.service.WXMsgService;
import com.qiaochu.mallchat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class ScanHandler extends AbstractHandler {

    @Resource
    private WXMsgService wxMsgService;


    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 扫码事件处理
        return wxMsgService.scan(wxMpXmlMessage);

    }

}
