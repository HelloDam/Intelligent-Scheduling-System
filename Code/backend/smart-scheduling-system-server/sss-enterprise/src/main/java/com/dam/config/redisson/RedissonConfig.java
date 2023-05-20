package com.dam.config.redisson;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    /**
     * 单节点Redisson配置
     *
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {
        //// 创建配置
        Config config = new Config();
        // 拼接语法
        String redisAddress = String.format("redis://%s:%s", host, port);
        //使用新的数据库，和做缓存的数据库隔开 useSingleServer（单节点）
        config.useSingleServer().setAddress(redisAddress).setDatabase(3).setPassword(password);
        //// 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}