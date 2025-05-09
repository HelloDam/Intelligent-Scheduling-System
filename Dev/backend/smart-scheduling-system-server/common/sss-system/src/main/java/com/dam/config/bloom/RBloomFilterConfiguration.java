package com.dam.config.bloom;

import com.dam.constant.RedisConstant;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author dam
 * @create 2024/3/14 21:10
 */
@Configuration
public class RBloomFilterConfiguration {
    /**
     * 防止用户注册查询数据库的布隆过滤器
     * 判断用户名是否存在使用
     */
    @Bean("userRegisterCachePenetrationBloomFilter")
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter(RedisConstant.BLOOM_USER_REGISTER);
        // 参数1：预估布隆过滤器里面要存放多少个元素
        // 参数2：误判率（误判率越低，散列函数数量越多）
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
