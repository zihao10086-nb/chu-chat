package com.qiaochu.mallchat.common.common.event.listener;

import com.qiaochu.mallchat.common.common.event.UserOnlineEvent;
import com.qiaochu.mallchat.common.common.event.UserRegisterEvent;
import com.qiaochu.mallchat.common.user.dao.UserDao;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.domain.enums.UserActiveEnum;
import com.qiaochu.mallchat.common.user.service.IpService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserOnlineListener {

    @Resource
    private IpService ipService;
    @Resource
    private UserDao userDao;
    //发改名卡
    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class ,phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void saveDB(UserOnlineEvent event){

        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(UserActiveEnum.ONLINE.getStatus());
        userDao.updateById(update);
        //用户ip解析
        ipService.refreshIpDetailAsync(user.getId());
    }


}
