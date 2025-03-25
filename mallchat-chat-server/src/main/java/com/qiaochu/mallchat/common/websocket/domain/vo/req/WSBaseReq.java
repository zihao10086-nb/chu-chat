package com.qiaochu.mallchat.common.websocket.domain.vo.req;

import lombok.Data;

@Data
public class WSBaseReq {

    /**
     * @see
     */
    private Integer type;
    private String data;
}
