package com.qiaochu.mallchat.common.user.service;

import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.qiaochu.mallchat.common.user.domain.vo.resp.UserInfoResp;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    Void wearingBadge(Long uid, @NotNull Long itemId);
}
