package com.qiaochu.mallchat.common.user.service.cache;

import com.qiaochu.mallchat.common.user.dao.ItemConfigDao;
import com.qiaochu.mallchat.common.user.domain.entity.ItemConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.List;

@Component
public class ItemCache {

    @Resource
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item",key = "'itemsByType:'+#itemType")
    public List<ItemConfig> getByType(Integer itemType){
        return itemConfigDao.getByType(itemType);
    }
    @CacheEvict(cacheNames = "item",key = "'itemsByType:'+#itemType")
    public void evictByType(Integer itemType){
    }
}
