package com.dam.core.aop;

import com.dam.annotation.Idempotent;
import com.dam.core.IdempotentContext;
import com.dam.core.exception.RepeatConsumptionException;
import com.dam.core.handler.IdempotentExecuteHandler;
import com.dam.core.handler.IdempotentExecuteHandlerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 幂等注解 AOP 拦截器
 */
@Aspect
public final class IdempotentAspect {

    /**
     * 使用Around来对Idempotent注解标记的方法进行环绕增强
     * @param joinPoint 使用 @Around ，自定义的切入点
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.dam.annotation.Idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        Idempotent idempotent = getIdempotent(joinPoint);
        // 根据注解来获取相应的处理器
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());
        // 存储实际方法的执行结果
        Object resultObj;
        try {
            // 当不满足幂等时，execute会报错，后面的代码不会再执行
            instance.execute(joinPoint, idempotent);
            // 请求的具体方法执行
            resultObj = joinPoint.proceed();
            // 处理器进行后处理，如解除分布式锁
            instance.postProcessing();
        } catch (RepeatConsumptionException ex) {
            /**
             * 触发幂等逻辑时可能有两种情况：
             *  1. 消息还在处理，但是不确定是否执行成功，那么需要返回错误，方便 RocketMQ 再次通过重试队列投递
             *  2. 消息处理成功了，该消息直接返回成功即可
             */
            if (!ex.getError()) {
                return null;
            }
            throw ex;
        } catch (Throwable ex) {
            // 客户端消费存在异常，需要删除幂等标识方便下次 RocketMQ 再次通过重试队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            IdempotentContext.clean();
        }
        return resultObj;
    }

    /**
     * 从给定的ProceedingJoinPoint中获取目标方法上的Idempotent注解实例
     *
     * @param joinPoint 切点对象，包含方法签名等信息
     * @return 目标方法上的Idempotent注解实例
     * @throws NoSuchMethodException 如果找不到对应方法时抛出此异常
     */
    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        // 获取方法签名对象，包含方法名、参数类型等信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 通过反射获取目标类（切面所拦截的对象）上与当前方法签名匹配的Method对象
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        // 从Method对象上获取Idempotent注解实例
        return targetMethod.getAnnotation(Idempotent.class);
    }
}
