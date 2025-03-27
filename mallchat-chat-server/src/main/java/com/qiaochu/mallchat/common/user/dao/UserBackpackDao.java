package com.qiaochu.mallchat.common.user.dao;

import com.qiaochu.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.qiaochu.mallchat.common.user.domain.entity.UserBackpack;
import com.qiaochu.mallchat.common.user.mapper.UserBackpackMapper;
import com.qiaochu.mallchat.common.user.service.IUserBackpackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2025-03-26
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack>  {

    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.No.getStatus())
                .count();
    }

    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.No.getStatus())
                .orderByAsc(UserBackpack::getId)
                .last("limit 1")
                .one();
    }

    public boolean useItem(UserBackpack item) {
        return lambdaUpdate()
                .eq(UserBackpack::getId, item.getId())
                .eq(UserBackpack::getStatus, YesOrNoEnum.No.getStatus())
                .set(UserBackpack::getStatus, YesOrNoEnum.Yes.getStatus())
                .update();
    }

    public List<UserBackpack> getByItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getStatus, YesOrNoEnum.No.getStatus())
                .in(UserBackpack::getItemId, itemIds)
                .list();
    }
}
