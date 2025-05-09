package com.dam.core.handler;

import com.dam.annotation.Idempotent;
import com.dam.core.aop.IdempotentParam;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 幂等执行处理器
 *
 */
public interface IdempotentExecuteHandler {

    IdempotentParam buildParam(ProceedingJoinPoint joinPoint);

    /**
     * 幂等处理逻辑
     *
     * @param wrapper 幂等参数包装器
     */
    void handler(IdempotentParam wrapper);

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    default void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent){
        // 模板方法模式：构建幂等参数包装器
        IdempotentParam idempotentParam = buildParam(joinPoint).setIdempotent(idempotent);
        // 如果不满足幂等，handler方法会通过抛出异常来使下面的程序被中断
        handler(idempotentParam);
    };

    /**
     * 幂等异常流程处理
     */
    default void exceptionProcessing() {

    }

    /**
     * 执行目标方法成功的后置处理
     */
    default void postProcessing() {

    }
}
