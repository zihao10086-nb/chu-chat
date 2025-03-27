package com.qiaochu.mallchat.common.common.Exception;


import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Charsets;
import com.qiaochu.mallchat.common.common.domain.vo.resp.ApiResult;
import com.qiaochu.mallchat.common.common.utils.JsonUtils;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public enum HttpErrorEnum {

    ACCESS_DENIED(401,"登录失效，请重新登录");

    private final Integer httpCode;
    private final String desc;

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(httpCode);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JSONUtil.toJsonStr(ApiResult.fail(httpCode,desc)));
    }
}
