package com.dam.core.handler.spel.mq;

import com.dam.annotation.Idempotent;
import com.dam.core.IdempotentContext;
import com.dam.core.aop.IdempotentAspect;
import com.dam.core.aop.IdempotentParam;
import com.dam.core.exception.RepeatConsumptionException;
import com.dam.core.handler.spel.IdempotentSpELService;
import com.dam.enums.IdempotentMQConsumeStatusEnum;
import com.dam.toolkit.LogUtil;
import com.dam.toolkit.SpELUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 MQ 场景
 * 这个类主要用于处理消息队列（MQ）中的重复消息问题，确保每条消息只被处理一次。
 */
@RequiredArgsConstructor
public final class IdempotentSpELByMQExecuteHandler implements IdempotentSpELService {

    private final StringRedisTemplate redisTemplate;
    /**
     * 设置一个常量表示 Redis 中存储的状态的超时时间，这里设置为10分钟
     */
    private final static int TIMEOUT = 600;
    /**
     * 用于在上下文中保存包装对象的键
     */
    private final static String WRAPPER = "wrapper:spEL:MQ";

    /**
     * 构建 IdempotentParam 对象，该对象包含了用于幂等性检查所需的信息。
     *
     * @param joinPoint AOP 中的连接点，代表了被通知的方法调用。
     * @return 返回构建好的 IdempotentParam 对象。
     */
    @SneakyThrows
    @Override
    public IdempotentParam buildParam(ProceedingJoinPoint joinPoint) {
        // 获取方法上的 Idempotent 注解
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        // 解析出幂等性的 key，基于方法参数和注解中的表达式
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        // 构建并返回 IdempotentParam 对象
        return IdempotentParam.builder().lockKey(key).joinPoint(joinPoint).build();
    }

    /**
     * 幂等逻辑判断，
     *
     * @param wrapper 包含了幂等性检查所需的参数的包装对象。
     */
    @Override
    public void handler(IdempotentParam wrapper) {
        // 生成唯一的幂等性键
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        // 尝试将唯一键设置为正在消费的状态，并设置过期时间
        Boolean setIfAbsent = redisTemplate
                .opsForValue()
                // 将key设置为正在消费状态
                .setIfAbsent(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMING.getCode(), TIMEOUT, TimeUnit.SECONDS);
        if (setIfAbsent != null && !setIfAbsent) {
            // --if--如果键已存在，则获取其状态并判断是否需要抛出异常
            String consumeStatus = redisTemplate.opsForValue().get(uniqueKey);
            // 判断是否消费失败
            boolean error = IdempotentMQConsumeStatusEnum.isError(consumeStatus);
            if (error) {
                // 记录警告日志
                LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ 重复消费, {}.", uniqueKey, "等待客户端延迟消费");
                // 抛出异常，删除幂等性键，待消息队列重新投递来消费
                throw new RepeatConsumptionException(error);
            }
            // 记录警告日志
            LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ 重复消费, {}.", uniqueKey, "消费已完成");

        }
        // 将包装对象存入上下文
        IdempotentContext.put(WRAPPER, wrapper);
    }

    /**
     * 在客户端消费过程中发生异常时，需要清理幂等标识以便下次消息重试时能正常处理
     */
    @Override
    public void exceptionProcessing() {
        // 从上下文中获取包装对象
        IdempotentParam wrapper = (IdempotentParam) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            // 获取幂等性注解
            Idempotent idempotent = wrapper.getIdempotent();
            // 生成唯一的幂等性键
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                // 删除 Redis 中的幂等性键
                redisTemplate.delete(uniqueKey);
            } catch (Throwable ex) {
                // 记录错误日志
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] 删除MQ防重令牌失败。", uniqueKey);
            }
        }
    }

    /**
     * 消息消费完成的后处理
     */
    @Override
    public void postProcessing() {
        // 从上下文中获取包装对象
        IdempotentParam wrapper = (IdempotentParam) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            // 获取幂等性注解
            Idempotent idempotent = wrapper.getIdempotent();
            // 生成唯一的幂等性键
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                // 更新幂等性键的状态为消费完成，并设置过期时间
                redisTemplate.opsForValue().set(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMED.getCode(), idempotent.keyTimeout(), TimeUnit.SECONDS);
            } catch (Throwable ex) {
                // 记录错误日志
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}]  删除MQ防重令牌失败。", uniqueKey);
            }
        }
    }
}
