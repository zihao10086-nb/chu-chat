package com.qiaochu.mallchat.common.websocket.domain.vo.resp;

import lombok.Data;

/**
 * Description:
 * Date: 2023-08-27
 */
@Data
public class WSBaseResp<T> {
    /**
     * @see com.qiaochu.mallchat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
