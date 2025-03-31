package com.qiaochu.mallchat.common.common.service;

import com.qiaochu.mallchat.common.common.Exception.BusinessException;
import com.qiaochu.mallchat.common.common.Exception.CommonErrorEnum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


@Service
public class LockService {

    @Resource
    private RedissonClient redissonClient;

    @SneakyThrows
    public <T> T executeWithLock(String key, int waitTime, TimeUnit timeUnit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        boolean success = lock.tryLock(waitTime, timeUnit);
        if (!success) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    @SneakyThrows
    public <T> T executeWithLock(String key, Supplier<T> supplier) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, supplier);
    }

    @SneakyThrows
    public <T> T executeWithLock(String key, Runnable runnable) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, () -> {
            runnable.run();
            return null;
        });

    }

    @FunctionalInterface
    public interface Supplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }
}
