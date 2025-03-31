package com.qiaochu.mallchat.common.common.event.listener;

import com.qiaochu.mallchat.common.common.event.UserRegisterEvent;
import com.qiaochu.mallchat.common.user.dao.UserDao;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.domain.enums.IdempotentEnum;
import com.qiaochu.mallchat.common.user.domain.enums.ItemEnum;
import com.qiaochu.mallchat.common.user.service.IUserBackpackService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserRegisterListener {

    @Resource
    private IUserBackpackService userBackpackService;
    @Resource
    private UserDao userDao;
    //发改名卡
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class ,phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event){
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID,user.getId().toString());
    }

    //发注册徽章
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class ,phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event){
        User user = event.getUser();
        //前100名  前10名
        int count = userDao.count();
        if (count < 10){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID,user.getId().toString());
        }else if (count < 100){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID,user.getId().toString());
        }
    }


}
