package com.dam.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @Author dam
 * @create 2024/4/2 16:51
 */
@Data
@ConfigurationProperties(prefix = "sss.idempotent.redis")
public class IdempotentRedisProperties {

    /**
     * Key 前缀
     */
    private String prefix = "";

    /**
     * Key 前缀字符集
     */
    private String prefixCharset = "UTF-8";

    /**
     * 默认超时时间
     */
    private Long valueTimeout = 30000L;

    /**
     * 时间单位
     */
    private TimeUnit valueTimeUnit = TimeUnit.MILLISECONDS;
}
