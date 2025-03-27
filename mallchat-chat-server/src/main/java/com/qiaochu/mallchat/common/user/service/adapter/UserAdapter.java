package com.qiaochu.mallchat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.qiaochu.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.qiaochu.mallchat.common.user.domain.entity.ItemConfig;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.domain.entity.UserBackpack;
import com.qiaochu.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.qiaochu.mallchat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdapter {
    public static User buildUserSave(String openId) {
        return User.builder().openId(openId).build();
    }

    public static User buildAuthorzeUser(Long uid, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(uid);
        user.setName(userInfo.getNickname());
        user.setAvatar(userInfo.getHeadImgUrl());
        return user;
    }

    public static UserInfoResp buildUserInfo(User user, Integer modifyNameCount) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(user, userInfoResp);
        userInfoResp.setModifyNameChance(modifyNameCount);
        return userInfoResp;
    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());

        return itemConfigs.stream().map(a->{
            BadgeResp badgeResp = new BadgeResp();
            BeanUtil.copyProperties(a, badgeResp);
            badgeResp.setObtain(obtainItemSet.contains(a.getId())? YesOrNoEnum.Yes.getStatus() : YesOrNoEnum.No.getStatus());
            badgeResp.setWearing(Objects.equals(a.getId(),user.getItemId())? YesOrNoEnum.Yes.getStatus() : YesOrNoEnum.No.getStatus());
            return badgeResp;
        }).sorted(Comparator.comparing(BadgeResp::getWearing,Comparator.reverseOrder())
                .thenComparing(BadgeResp::getObtain,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
