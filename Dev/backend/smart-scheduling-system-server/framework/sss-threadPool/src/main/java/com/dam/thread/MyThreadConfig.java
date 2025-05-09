package com.dam.thread;

import com.dam.eager.EagerThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {

    /**
     * @param poolConfigProperties 如果需要使用到ThreadPoolConfigProperties，一定要使用Component将其加入到容器中
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties poolConfigProperties) {
        // 普通线程池
//        return new ThreadPoolExecutor(poolConfigProperties.getCoreSize(),
//                poolConfigProperties.getMaxSize(),
//                poolConfigProperties.getKeepAliveTime(),
//                TimeUnit.SECONDS,
//                //队列的最大容量
//                new LinkedBlockingDeque<>(600),
//                //使用默认的工程
//                Executors.defaultThreadFactory(),
//                //使用拒绝新来的拒绝策略
//                new ThreadPoolExecutor.CallerRunsPolicy()
//        );

        // 快速消费线程池
        return EagerThreadPoolExecutor.createEagerThreadPoolExecutor(
                poolConfigProperties.getCoreSize(),
                poolConfigProperties.getMaxSize(),
                poolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                // 队列的最大容量
                600,
                // 使用默认的工程
                Executors.defaultThreadFactory(),
                // 使用拒绝新来的拒绝策略
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
