package com.dam.config;

import com.dam.core.aop.IdempotentAspect;
import com.dam.core.handler.param.IdempotentParamExecuteHandler;
import com.dam.core.handler.param.IdempotentParamService;
import com.dam.core.handler.spel.IdempotentSpELService;
import com.dam.core.handler.spel.mq.IdempotentSpELByMQExecuteHandler;
import com.dam.core.handler.spel.restful.IdempotentSpELByRestAPIExecuteHandler;
import com.dam.core.handler.token.IdempotentTokenExecuteHandler;
import com.dam.core.handler.token.IdempotentTokenService;
import com.dam.properties.IdempotentTokenProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 幂等自动装配
 */
@EnableConfigurationProperties(IdempotentTokenProperties.class)
@Configuration
public class IdempotentAutoConfiguration {

    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * 参数方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentParamService idempotentParamExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentParamExecuteHandler(redissonClient);
    }

    /**
     * Token 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentTokenService idempotentTokenExecuteHandler(StringRedisTemplate distributedCache,
                                                                IdempotentTokenProperties idempotentTokenProperties) {
        return new IdempotentTokenExecuteHandler(distributedCache, idempotentTokenProperties);
    }

    /**
     * 申请幂等 Token 控制器，基于 RestAPI 场景
     */
//    @Bean
//    public IdempotentTokenController idempotentTokenController(IdempotentTokenService idempotentTokenService) {
//        return new IdempotentTokenController(idempotentTokenService);
//    }

    /**
     * SpEL 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELService idempotentSpELByRestAPIExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentSpELByRestAPIExecuteHandler(redissonClient);
    }

    /**
     * SpEL 方式幂等实现，基于 MQ 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELByMQExecuteHandler idempotentSpELByMQExecuteHandler(StringRedisTemplate distributedCache) {
        return new IdempotentSpELByMQExecuteHandler(distributedCache);
    }
}
