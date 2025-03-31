package com.qiaochu.mallchat.common.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.qiaochu.mallchat.common.common.domain.vo.resp.ApiResult;
import com.qiaochu.mallchat.common.common.utils.JsonUtils;
import com.qiaochu.mallchat.common.user.dao.UserDao;
import com.qiaochu.mallchat.common.user.domain.entity.IpDetail;
import com.qiaochu.mallchat.common.user.domain.entity.IpInfo;
import com.qiaochu.mallchat.common.user.domain.entity.User;
import com.qiaochu.mallchat.common.user.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class IpServiceImpl implements IpService , DisposableBean {
    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(500), new NamedThreadFactory("refresh-ipDetail", false));

    @Resource
    private UserDao userDao;
    @Override
    public void refreshIpDetailAsync(Long uid) {
        executor.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (Objects.isNull(ipInfo)){
                return;
            }
            String ip = ipInfo.needRefresh();
            //刷新ip信息
            if(StringUtils.isBlank(ip)){
                return;
            }
            IpDetail ipDetail = tryGetIpDetailOrNullThreeTimes(ip);
            if (Objects.nonNull(ipDetail)){
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
            }
        });
    }

    private static IpDetail tryGetIpDetailOrNullThreeTimes(String ip) {
        for (int i = 0; i < 3; i++){
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (Objects.nonNull(ipDetail)){
                return ipDetail;
            }
            //休眠2秒
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("tryGetIpDetailOrNullThreeTimes InterruptedException ",e);
            }
        }
        return null;
    }

    private static IpDetail getIpDetailOrNull(String ip) {
        try {
            String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
            ApiResult<IpDetail> result = JsonUtils.toObj(body, new TypeReference<ApiResult<IpDetail>>() {});
            return result.getData();
        }catch (Exception e){
            return null;
        }
    }

    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                IpDetail ipDetail = tryGetIpDetailOrNullThreeTimes("192.168.127.12");
                if (Objects.nonNull(ipDetail)){
                    Date date = new Date();
                    System.out.println(String.format("ip:%s, time:%s ms", ipDetail.getIp(), date.getTime() - begin.getTime()));
                }
            });
        }
    }
    //线程池优雅停机
    @Override
    public void destroy() throws Exception {
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {//最多等30秒，处理不完就拉倒
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", executor);
            }
        }
    }
}
