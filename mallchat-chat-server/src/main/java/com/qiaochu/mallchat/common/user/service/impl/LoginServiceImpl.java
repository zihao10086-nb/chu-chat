package com.qiaochu.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.qiaochu.mallchat.common.common.constant.RedisKey;
import com.qiaochu.mallchat.common.common.utils.JwtUtils;
import com.qiaochu.mallchat.common.common.utils.RedisUtils;
import com.qiaochu.mallchat.common.user.service.LoginService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    private static final int TOKEN_EXPIRE_DAYS = 3;
    private static final int TOKEN_RENEWAL_DAYS = 1;
    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean verify(String token) {
        return false;
    }

    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long validUid = getValidUid(token);
        String userTokenKey = getUserTokenKey(validUid);
        Long expire = RedisUtils.getExpire(userTokenKey,TimeUnit.DAYS);
        if (expire==-2){
            //不存在的key
            return;
        }
        if (expire< TOKEN_RENEWAL_DAYS){
            RedisUtils.expire(userTokenKey, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long id) {
        String token = jwtUtils.createToken(id);
        RedisUtils.set(getUserTokenKey(id), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)){
            return null;
        }
        String oldToken = RedisUtils.get(getUserTokenKey(uid));
        if (StrUtil.isBlank(oldToken)){
            return null;
        }
        return Objects.equals(oldToken, token)? uid : null;
    }

    private String getUserTokenKey(Long uid){
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }
}
