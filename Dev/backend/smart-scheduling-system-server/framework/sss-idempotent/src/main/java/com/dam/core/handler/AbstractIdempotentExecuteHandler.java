//package com.dam.core.handler;
//
//import com.dam.annotation.Idempotent;
//import com.dam.core.aop.IdempotentParam;
//import org.aspectj.lang.ProceedingJoinPoint;
//
///**
// * 抽象幂等执行处理器
// *
// */
//public abstract class AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler {
//
//    /**
//     * 构建幂等验证过程中所需要的参数包装器
//     *
//     * @param joinPoint AOP 方法处理
//     * @return 幂等参数包装器
//     */
//    protected abstract IdempotentParam buildParam(ProceedingJoinPoint joinPoint);
//
//    /**
//     * 执行幂等处理逻辑
//     *
//     * @param joinPoint  AOP 方法处理
//     * @param idempotent 幂等注解
//     */
//    @Override
//    public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
//        // 模板方法模式：构建幂等参数包装器
//        IdempotentParam idempotentParam = buildParam(joinPoint).setIdempotent(idempotent);
//        // 如果不满足幂等，handler方法会通过抛出异常来使下面的程序被中断
//        handler(idempotentParam);
//    }
//}
