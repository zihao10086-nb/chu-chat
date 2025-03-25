package com.qiaochu.mallchat.common.user.service;

import com.qiaochu.mallchat.common.user.domain.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2025-03-24
 */
public interface UserService{

    Long register(User userSave);
}
