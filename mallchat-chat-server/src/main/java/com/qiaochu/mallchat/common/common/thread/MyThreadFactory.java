package com.qiaochu.mallchat.common.common.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();
    private ThreadFactory originalThreadFactory;
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = originalThreadFactory.newThread(r);//执行spring线程自己的创建模式
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);//额外装饰我们的异常处理器
        return thread;
    }
}
