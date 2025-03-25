package com.qiaochu.mallchat.common.user.service.impl;

import com.qiaochu.mallchat.common.user.dao.UserDao;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Override
    @Transactional
    public Long register(User userSave) {
        boolean save = userDao.save(userSave);
        //todo 用户注册
        return userSave.getId();
    }
}
