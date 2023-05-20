package com.dam.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {

    /**
     *
     * @param poolConfigProperties 如果需要使用到ThreadPoolConfigProperties，一定要使用Component将其加入到容器中
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties poolConfigProperties) {
        return new ThreadPoolExecutor(poolConfigProperties.getCoreSize(),
                poolConfigProperties.getMaxSize(),
                poolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                //队列的最大容量
                new LinkedBlockingDeque<>(100000),
                //使用默认的工程
                Executors.defaultThreadFactory(),
                //使用拒绝新来的拒绝策略
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
