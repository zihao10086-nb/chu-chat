package com.qiaochu.mallchat.common.user.service.impl;

import com.qiaochu.mallchat.common.common.Exception.BusinessException;
import com.qiaochu.mallchat.common.common.utils.AssertUtil;
import com.qiaochu.mallchat.common.user.dao.ItemConfigDao;
import com.qiaochu.mallchat.common.user.dao.UserBackpackDao;
import com.qiaochu.mallchat.common.user.dao.UserDao;
import com.qiaochu.mallchat.common.user.domain.entity.ItemConfig;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.domain.entity.UserBackpack;
import com.qiaochu.mallchat.common.user.domain.enums.ItemEnum;
import com.qiaochu.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.qiaochu.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.qiaochu.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.qiaochu.mallchat.common.user.service.UserService;
import com.qiaochu.mallchat.common.user.service.adapter.UserAdapter;
import com.qiaochu.mallchat.common.user.service.cache.ItemCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private ItemConfigDao itemConfigDao;
    @Resource
    private ItemCache itemCache;
    @Override
    @Transactional
    public Long register(User userSave) {
        boolean save = userDao.save(userSave);
        //todo 用户注册
        return userSave.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer modifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "用户名已存在");
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡不够了");
        //使用改名卡
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success){
            //改名
            userDao.modifyName(uid,name);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        //查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //用户当前佩戴的徽章
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);

    }

    @Override
    public Void wearingBadge(Long uid, Long itemId) {
        //确保有徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(firstValidItem, "您还没有这个徽章，快去获得吧");
        //确保这个物品是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(),ItemTypeEnum.BADGE.getType(),"只有徽章才能佩戴");
        userDao.wearingBadge(uid, itemId);

    }

}
