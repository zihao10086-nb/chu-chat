package com.qiaochu.mallchat.common.websocket.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSBlack {
    private Long uid;
}
