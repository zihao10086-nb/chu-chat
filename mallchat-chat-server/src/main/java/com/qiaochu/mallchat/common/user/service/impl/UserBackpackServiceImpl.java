package com.qiaochu.mallchat.common.user.service.impl;

import com.qiaochu.mallchat.common.common.annotation.RedissonLock;
import com.qiaochu.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.qiaochu.mallchat.common.common.service.LockService;
import com.qiaochu.mallchat.common.common.utils.AssertUtil;
import com.qiaochu.mallchat.common.user.dao.UserBackpackDao;
import com.qiaochu.mallchat.common.user.domain.entity.UserBackpack;
import com.qiaochu.mallchat.common.user.domain.enums.IdempotentEnum;
import com.qiaochu.mallchat.common.user.service.IUserBackpackService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements IUserBackpackService {
    @Resource
    private LockService lockService;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        //发放物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.No.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
