package com.dam.core.handler.param;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.dam.constant.KeyConstants;
import com.dam.context.UserContext;
import com.dam.core.IdempotentContext;
import com.dam.core.aop.IdempotentParam;
import com.dam.exception.SSSException;
import com.dam.model.enums.ResultCodeEnum;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 基于方法参数验证请求幂等性
 * 基于分布式锁实现
 */
@RequiredArgsConstructor//使用构造器注入
public final class IdempotentParamExecuteHandler implements IdempotentParamService {

    /**
     * 注入Redisson客户端，用于操作分布式锁
     */
    private final RedissonClient redissonClient;

    /**
     * 获取到的分布式锁对应的key，用来给上下文使用
     */
    private final static String CONTEXT_LOCK_KEY = KeyConstants.IDEMPOTENT_PREFIX + "lock:param:restAPI";

    @Override
    public IdempotentParam buildParam(ProceedingJoinPoint joinPoint) {
        // 构建分布式锁的唯一key，包含请求路径、当前用户ID和参数MD5值
        String lockKey = String.format(KeyConstants.IDEMPOTENT_PREFIX + "lock:param:path:%s:currentUserId:%s:md5:%s", getServletPath(), getCurrentUserId(), calcArgsMD5(joinPoint));
        return IdempotentParam.builder().lockKey(lockKey).joinPoint(joinPoint).build();
    }

    /**
     * 幂等逻辑判断，如果获取分布式锁失败，说明同样的请求和参数已经在执行，返回异常
     *
     * @param wrapper 幂等参数包装器
     */
    @Override
    public void handler(IdempotentParam wrapper) {
        // 从包装器中获取分布式锁对应的key
        String lockKey = wrapper.getLockKey();
        // 通过RedissonClient获取分布式锁实例
        RLock lock = redissonClient.getLock(lockKey);
        // 尝试获取锁，如果无法立即获取（即锁已被其他请求持有），则抛出异常，表示请求重复
        if (!lock.tryLock()) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), wrapper.getIdempotent().message());
        }
        // 将获取到的锁存储在IdempotentContext中，以便后续解锁操作
        IdempotentContext.put(CONTEXT_LOCK_KEY, lock);
    }

    /**
     * 将分布式锁进行解锁
     */
    @Override
    public void postProcessing() {
        RLock lock = null;
        try {
            // 从IdempotentContext中获取之前存储的锁实例
            lock = (RLock) IdempotentContext.getKey(CONTEXT_LOCK_KEY);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }


    /**
     * 获取当前线程上下文中的ServletPath（请求路径）
     *
     * @return 当前线程上下文 ServletPath
     */
    private String getServletPath() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 从ServletAttributes中获取请求的ServletPath
        return sra.getRequest().getServletPath();
    }

    /**
     * 获取当前操作用户的ID
     *
     * @return 当前操作用户 ID
     */
    private String getCurrentUserId() {
        // 从UserContext中获取当前用户的ID
        String userId = UserContext.getUserId();
        if (StrUtil.isBlank(userId)) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "用户ID获取失败，请登录");
        }
        return userId;
    }

    /**
     * 计算参数的MD5码
     *
     * @param joinPoint 包含方法参数的ProceedingJoinPoint对象
     * @return 参数的MD5值，用于标识请求参数的唯一性
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        String md5 = DigestUtil.md5Hex(JSON.toJSONBytes(joinPoint.getArgs()));
        return md5;
    }
}
