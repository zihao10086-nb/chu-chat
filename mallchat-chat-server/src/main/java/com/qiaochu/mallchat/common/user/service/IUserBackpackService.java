package com.qiaochu.mallchat.common.user.service;

import com.qiaochu.mallchat.common.user.domain.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2025-03-26
 */
public interface IUserBackpackService{
    /**
     * 给用户发放物品
     * @param userId 用户id
     * @param itemId 物品id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);

}
