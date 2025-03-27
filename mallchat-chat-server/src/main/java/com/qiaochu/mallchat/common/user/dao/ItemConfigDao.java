package com.qiaochu.mallchat.common.user.dao;

import com.qiaochu.mallchat.common.user.domain.entity.ItemConfig;
import com.qiaochu.mallchat.common.user.mapper.ItemConfigMapper;
import com.qiaochu.mallchat.common.user.service.IItemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2025-03-26
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

    public List<ItemConfig> getByType(Integer itemType) {
        return lambdaQuery()
                .eq(ItemConfig::getType, itemType)
                .list();
    }
}
