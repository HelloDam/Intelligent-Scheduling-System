//package com.dam.config.cache;
//
//import org.springframework.context.annotation.Configuration;
//
//import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.cache.CacheProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
////当前配置类和CacheProperties配置的绑定生效
//@EnableConfigurationProperties(CacheProperties.class)
//@Configuration
//public class SSSCacheConfig {
//
//    @Autowired
//    CacheProperties cacheProperties;
//
//    /**
//     * cache数据序列化方式修改为json方式
//     * @return
//     */
//    @Bean
//    RedisCacheConfiguration redisCacheConfiguration() {
//        //先获取默认配置
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//        //设置key序列化类型为String类型
//        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
//        //设置value序列化类型为Json类型
//        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//
//        //// //让配置文件中的配置生效，设置ttl
//        //获取redis的所有配置
//        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
//        //ttl
//        if (redisProperties.getTimeToLive() != null) {
//            config = config.entryTtl(redisProperties.getTimeToLive());
//        }
//        if (redisProperties.getKeyPrefix() != null) {
//            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
//        }
//        if (!redisProperties.isCacheNullValues()) {
//            config = config.disableCachingNullValues();
//        }
//        if (!redisProperties.isUseKeyPrefix()) {
//            config = config.disableKeyPrefix();
//        }
//        return config;
//    }
//
//}
