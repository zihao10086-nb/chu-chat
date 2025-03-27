package com.qiaochu.mallchat.common.common.interceptor;

import cn.hutool.http.ContentType;
import com.google.common.base.Charsets;
import com.qiaochu.mallchat.common.common.Exception.HttpErrorEnum;
import com.qiaochu.mallchat.common.user.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String UID = "uid";
    @Resource
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)){
            // 用户已经登录，设置uid到request中
            request.setAttribute(UID, validUid);
        }else {
            //用户未登录
            boolean isPublicURI = isPublicURI(request);
            if (!isPublicURI){
                //401
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;

    }

    private static boolean isPublicURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        return split.length>3 && "public".equals(split[3]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replace(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }
}
