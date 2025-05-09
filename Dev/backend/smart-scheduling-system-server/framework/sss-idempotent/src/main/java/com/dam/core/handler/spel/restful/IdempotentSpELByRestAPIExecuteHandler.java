package com.dam.core.handler.spel.restful;

import com.dam.annotation.Idempotent;
import com.dam.constant.KeyConstants;
import com.dam.core.IdempotentContext;
import com.dam.core.aop.IdempotentAspect;
import com.dam.core.aop.IdempotentParam;
import com.dam.core.handler.spel.IdempotentSpELService;
import com.dam.exception.SSSException;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.toolkit.SpELUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 RestAPI(Restful) 场景
 * 基于分布式锁实现
 */
@RequiredArgsConstructor
public final class IdempotentSpELByRestAPIExecuteHandler implements IdempotentSpELService {

    /**
     * Redisson 客户端，用于操作分布式锁
     */
    private final RedissonClient redissonClient;

    /**
     * 分布式锁的基础键名，用于存储全局唯一标识的锁
     */
    private final static String LOCK = KeyConstants.IDEMPOTENT_PREFIX + "lock:spEL:restAPI";

    /**
     * 构建幂等参数包装器，通过解析 SpEL 表达式生成请求的唯一标识（锁键）
     *
     * @param joinPoint 切点对象，包含目标方法信息及参数
     * @return 构建好的幂等参数包装器
     */
    @SneakyThrows
    @Override
    public IdempotentParam buildParam(ProceedingJoinPoint joinPoint) {
        // 从切点对象中获取方法上的 @Idempotent 注解
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        // 使用 SpEL 工具解析注解中的 key 属性表达式，生成请求的唯一标识（锁键）
        String lockKey = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
//        System.out.println("lockKey:" + lockKey);
        return IdempotentParam.builder().lockKey(lockKey).joinPoint(joinPoint).build();
    }

    /**
     * 幂等性逻辑处理。尝试获取分布式锁，如果获取失败（即锁已被其他请求持有），说明有相同方法和参数的请求正在执行，
     * 此时抛出异常，拒绝当前请求。
     *
     * @param wrapper 幂等参数包装器
     */
    @Override
    public void handler(IdempotentParam wrapper) {
//        System.out.println("wrapper.getLockKey():" + wrapper.getLockKey());
        String uniqueKey = KeyConstants.IDEMPOTENT_PREFIX + wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        RLock lock = redissonClient.getLock(uniqueKey);
        // 尝试获取锁，如果无法立即获取（即锁已被其他请求持有），抛出异常，表示请求重复
        if (!lock.tryLock()) {
//            System.out.println("wrapper.getIdempotent().message():" + wrapper.getIdempotent().message());
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), wrapper.getIdempotent().message());
        }
        // 上下文用来传递分布式锁，便于请求处理完成之后进行解锁
        IdempotentContext.put(LOCK, lock);
    }

    /**
     * 后处理，对分布式锁进行解锁
     */
    @Override
    public void postProcessing() {
        // 从 IdempotentContext 中获取之前存储的锁实例
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            // 如果锁实例不为空，进行解锁操作
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /**
     * 请求的方法执行过程中发生了异常，也对分布式锁进行解锁
     */
    @Override
    public void exceptionProcessing() {
        // 从 IdempotentContext 中获取之前存储的锁实例
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            // 如果锁实例不为空，进行解锁操作
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}
