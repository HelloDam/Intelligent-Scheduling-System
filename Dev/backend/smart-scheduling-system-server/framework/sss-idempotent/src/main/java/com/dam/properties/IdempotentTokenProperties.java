package com.dam.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * 幂等属性配置
 *
 */
@Data
@ConfigurationProperties(prefix = "sss.idempotent.token")
public class IdempotentTokenProperties {

    /**
     * Token 幂等 Key 前缀
     */
    private String prefix;

    /**
     * Token 申请后过期时间
     * 单位默认毫秒 {@link TimeUnit#MILLISECONDS}
     * 随着分布式缓存过期时间单位 {@link IdempotentRedisProperties#getValueTimeUnit()} 而变化
     */
    private Long timeout;
}
