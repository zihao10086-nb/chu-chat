package com.qiaochu.mallchat.common;

import com.qiaochu.mallchat.common.common.utils.JwtUtils;
import com.qiaochu.mallchat.common.common.utils.RedisUtils;
import com.qiaochu.mallchat.common.user.domain.enums.IdempotentEnum;
import com.qiaochu.mallchat.common.user.domain.enums.ItemEnum;
import com.qiaochu.mallchat.common.user.service.IUserBackpackService;
import com.qiaochu.mallchat.common.user.service.LoginService;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private JwtUtils jwtUtils;
    @Test
    public void testJwt() throws InterruptedException {
        System.out.println(jwtUtils.createToken(1L));
        System.out.println(jwtUtils.createToken(1L));
        System.out.println(jwtUtils.createToken(1L));
    }
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private IUserBackpackService iUserBackpackService;
    @Test
    public void acquireItem() {
        iUserBackpackService.acquireItem(20001L, ItemEnum.PLANET.getId(), IdempotentEnum.UID, "20001L");

    }
    @SneakyThrows
    @Test
    public void jwt() {
        String token = loginService.login(20001L);
        System.out.println(token);
    }
    @Autowired
    private LoginService loginService;
    @Test
    public void testRedis() {
        String key = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjIwMDAxLCJjcmVhdGVUaW1lIjoxNzQyODk0MjQxfQ.71BlxlpPyePYKuZzuh5zFq6tKOMva320wHsW0F6Tob8";
        Long validUid = loginService.getValidUid(key);
        System.out.println(validUid);
    }
    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }
}
